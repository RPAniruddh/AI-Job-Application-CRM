package com.jobcrm.email.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcrm.ai.client.OpenAiClient;
import com.jobcrm.ai.dto.ChatRequest;
import com.jobcrm.application.model.JobApplication;
import com.jobcrm.email.model.EmailDraft;
import com.jobcrm.email.repository.EmailDraftRepository;

@Service
public class EmailDraftingService {

    private static final Logger log = LoggerFactory.getLogger(EmailDraftingService.class);

    private final OpenAiClient openAiClient;
    private final EmailDraftRepository emailDraftRepository;
    private final ObjectMapper objectMapper;

    public EmailDraftingService(OpenAiClient openAiClient,
                                EmailDraftRepository emailDraftRepository,
                                ObjectMapper objectMapper) {
        this.openAiClient = openAiClient;
        this.emailDraftRepository = emailDraftRepository;
        this.objectMapper = objectMapper;
    }

    public EmailDraft draftFollowUpEmail(JobApplication application) {
        log.info("Drafting follow-up email for application: {}", application.getId());

        String prompt = buildFollowUpPrompt(application);
        ChatRequest chatRequest = new ChatRequest(
        	    "gpt-4o-mini",
        	    List.of(new ChatRequest.Message("user", prompt)),
        	    0.7
        	);
        	String rawResponse = openAiClient.chat(chatRequest).getFirstContent();
        Map<String, String> parsed = parseEmailResponse(rawResponse);

        EmailDraft draft = EmailDraft.builder()
                .user(application.getUser())
                .application(application)
                .subject(parsed.get("subject"))
                .body(parsed.get("body"))
                .build();

        return emailDraftRepository.save(draft);
    }

    public EmailDraft draftThankYouEmail(JobApplication application) {
        log.info("Drafting thank-you email for application: {}", application.getId());

        String prompt = buildThankYouPrompt(application);
        ChatRequest chatRequest = new ChatRequest(
        	    "gpt-4o-mini",
        	    List.of(new ChatRequest.Message("user", prompt)),
        	    0.7
        	);
        	String rawResponse = openAiClient.chat(chatRequest).getFirstContent();
        Map<String, String> parsed = parseEmailResponse(rawResponse);

        EmailDraft draft = EmailDraft.builder()
                .user(application.getUser())
                .application(application)
                .subject(parsed.get("subject"))
                .body(parsed.get("body"))
                .build();

        return emailDraftRepository.save(draft);
    }

    private String buildFollowUpPrompt(JobApplication application) {
        return String.format("""
                You are %s, a professional job seeker writing a follow-up email.
                Sign the email with your real name: %s

                Job Details:
                - Applicant Name: %s
                - Company: %s
                - Role: %s
                - Applied: %s
                - Notes: %s

                Write a concise, professional follow-up email checking on the application status.
                Keep it under 150 words. Be polite and enthusiastic but not desperate.
                Sign off with the applicant's real name.

                Respond ONLY with a JSON object in this exact format, no other text:
                {
                    "subject": "email subject here",
                    "body": "email body here"
                }
                """,
                application.getUser().getFullName(),
                application.getUser().getFullName(),
                application.getUser().getFullName(),
                application.getCompany(),
                application.getRoleTitle(),
                application.getAppliedDate(),
                application.getNotes() != null ? application.getNotes() : "N/A"
        );
    }

    private String buildThankYouPrompt(JobApplication application) {
        return String.format("""
                You are %s, a professional job seeker writing a thank-you email after an interview.
                Sign the email with your real name: %s

                Job Details:
                - Applicant Name: %s
                - Company: %s
                - Role: %s
                - Notes: %s

                Write a concise, professional thank-you email after an interview.
                Keep it under 150 words. Express genuine gratitude and reiterate interest.
                Sign off with the applicant's real name.

                Respond ONLY with a JSON object in this exact format, no other text:
                {
                    "subject": "email subject here",
                    "body": "email body here"
                }
                """,
                application.getUser().getFullName(),
                application.getUser().getFullName(),
                application.getUser().getFullName(),
                application.getCompany(),
                application.getRoleTitle(),
                application.getNotes() != null ? application.getNotes() : "N/A"
        );
    }

    public EmailDraft draftConfirmationEmail(JobApplication application) {
        log.info("Drafting confirmation email for application: {}", application.getId());
        String prompt = buildConfirmationPrompt(application);
        ChatRequest chatRequest = new ChatRequest(
            "gpt-4o-mini",
            List.of(new ChatRequest.Message("user", prompt)),
            0.7
        );
        String rawResponse = openAiClient.chat(chatRequest).getFirstContent();
        Map<String, String> parsed = parseEmailResponse(rawResponse);
        EmailDraft draft = EmailDraft.builder()
                .user(application.getUser())
                .application(application)
                .subject(parsed.get("subject"))
                .body(parsed.get("body"))
                .build();
        return emailDraftRepository.save(draft);
    }

    private String buildConfirmationPrompt(JobApplication application) {
        return String.format("""
                You are %s, a professional job seeker writing a confirmation email to a recruiter.
                You have just submitted your application and want to confirm it and express enthusiasm.

                Job Details:
                - Company: %s
                - Role: %s
                - Applied: %s
                - Notes: %s

                Write a brief, professional confirmation email (under 120 words) letting the recruiter
                know you have applied, expressing genuine enthusiasm, and inviting them to reach out.
                Do not use placeholders — sign with your real name: %s

                Respond ONLY with a JSON object in this exact format, no other text:
                {
                    "subject": "email subject here",
                    "body": "email body here"
                }
                """,
                application.getUser().getFullName(),
                application.getCompany(),
                application.getRoleTitle(),
                application.getAppliedDate(),
                application.getNotes() != null ? application.getNotes() : "N/A",
                application.getUser().getFullName()
        );
    }

    private Map<String, String> parseEmailResponse(String rawResponse) {
        try {
            String cleaned = rawResponse.strip();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("```json", "").replaceAll("```", "").strip();
            }
            return objectMapper.readValue(cleaned, Map.class);
        } catch (Exception e) {
            log.error("Failed to parse GPT email response: {}", rawResponse, e);
            return Map.of(
                    "subject", "Follow-up on your application",
                    "body", rawResponse
            );
        }
    }
}