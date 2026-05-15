package com.jobcrm.email.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcrm.ai.client.OpenAiClient;
import com.jobcrm.ai.dto.ChatRequest;
import com.jobcrm.ai.repository.UserResumeRepository;
import com.jobcrm.application.model.JobApplication;
import com.jobcrm.email.model.EmailDraft;
import com.jobcrm.email.repository.EmailDraftRepository;

@Service
public class EmailDraftingService {

    private static final Logger log = LoggerFactory.getLogger(EmailDraftingService.class);

    private final OpenAiClient openAiClient;
    private final EmailDraftRepository emailDraftRepository;
    private final ObjectMapper objectMapper;
    private final UserResumeRepository userResumeRepository;

    public EmailDraftingService(OpenAiClient openAiClient,
                                EmailDraftRepository emailDraftRepository,
                                ObjectMapper objectMapper,
                                UserResumeRepository userResumeRepository) {
        this.openAiClient = openAiClient;
        this.emailDraftRepository = emailDraftRepository;
        this.objectMapper = objectMapper;
        this.userResumeRepository = userResumeRepository;
    }

    public EmailDraft draftFollowUpEmail(JobApplication application) {
        log.info("Drafting follow-up email for application: {}", application.getId());
        String prompt = buildFollowUpPrompt(application);
        ChatRequest chatRequest = new ChatRequest(
            "gpt-4o-mini",
            List.of(new ChatRequest.Message("user", prompt)),
            0.9
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
            0.9
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

    public EmailDraft draftConfirmationEmail(JobApplication application) {
        log.info("Drafting confirmation email for application: {}", application.getId());
        String prompt = buildConfirmationPrompt(application);
        ChatRequest chatRequest = new ChatRequest(
            "gpt-4o-mini",
            List.of(new ChatRequest.Message("user", prompt)),
            0.9
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

    private String getResumeText(JobApplication application) {
        return userResumeRepository
            .findTopByUserIdOrderByCreatedAtDesc(application.getUser().getId())
            .map(resume -> resume.getResumeText().substring(0, Math.min(resume.getResumeText().length(), 800)))
            .orElse("Experienced software engineer with backend development skills.");
    }

    private String buildFollowUpPrompt(JobApplication application) {
        String resumeSnippet = getResumeText(application);
        String jdSnippet = application.getRawDescription() != null
            ? application.getRawDescription().substring(0, Math.min(application.getRawDescription().length(), 600))
            : "No description available";

        return String.format("""
                You are %s, writing a follow-up email after applying for a job.

                Your background (from resume):
                %s

                Job you applied for:
                - Company: %s
                - Role: %s
                - Applied on: %s
                - Job description excerpt: %s

                Write a highly personalized follow-up email (under 150 words) that:
                - References 1-2 specific skills from YOUR background that match THIS specific role
                - Mentions something specific about the company or role (not generic)
                - Sounds natural and human, not like a template
                - Signs off with your real name: %s
                - Does NOT use any placeholder text like [Your Name] or [Company]

                Respond ONLY with valid JSON, no markdown, no backticks:
                {"subject": "...", "body": "..."}
                """,
                application.getUser().getFullName(),
                resumeSnippet,
                application.getCompany(),
                application.getRoleTitle(),
                application.getAppliedDate(),
                jdSnippet,
                application.getUser().getFullName()
        );
    }

    private String buildThankYouPrompt(JobApplication application) {
        String resumeSnippet = getResumeText(application);
        String jdSnippet = application.getRawDescription() != null
            ? application.getRawDescription().substring(0, Math.min(application.getRawDescription().length(), 600))
            : "No description available";

        return String.format("""
                You are %s, writing a thank-you email after a job interview.

                Your background (from resume):
                %s

                Job you interviewed for:
                - Company: %s
                - Role: %s
                - Job description excerpt: %s

                Write a highly personalized thank-you email (under 150 words) that:
                - References something specific discussed or relevant to THIS role
                - Mentions 1 specific skill or experience from YOUR background relevant to this company
                - Sounds warm and genuine, not copy-paste
                - Signs off with your real name: %s
                - Does NOT use any placeholder text

                Respond ONLY with valid JSON, no markdown, no backticks:
                {"subject": "...", "body": "..."}
                """,
                application.getUser().getFullName(),
                resumeSnippet,
                application.getCompany(),
                application.getRoleTitle(),
                jdSnippet,
                application.getUser().getFullName()
        );
    }

    private String buildConfirmationPrompt(JobApplication application) {
        String resumeSnippet = getResumeText(application);
        String jdSnippet = application.getRawDescription() != null
            ? application.getRawDescription().substring(0, Math.min(application.getRawDescription().length(), 600))
            : "No description available";

        return String.format("""
                You are %s, writing a confirmation email to a recruiter after submitting a job application.

                Your background (from resume):
                %s

                Job you just applied for:
                - Company: %s
                - Role: %s
                - Applied on: %s
                - Job description excerpt: %s

                Write a brief, highly personalized confirmation email (under 120 words) that:
                - Confirms you have submitted your application
                - Highlights 1 specific skill or experience from YOUR background that directly matches this role
                - Expresses genuine enthusiasm for something specific about this company or position
                - Invites the recruiter to reach out with any questions
                - Signs off with your real name: %s
                - Does NOT use any placeholder text like [Your Name] or [Company]

                Respond ONLY with valid JSON, no markdown, no backticks:
                {"subject": "...", "body": "..."}
                """,
                application.getUser().getFullName(),
                resumeSnippet,
                application.getCompany(),
                application.getRoleTitle(),
                application.getAppliedDate(),
                jdSnippet,
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
