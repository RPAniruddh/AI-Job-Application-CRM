package com.jobcrm.email.service;

import com.jobcrm.config.AppProperties;
import com.jobcrm.email.model.EmailDraft;
import com.jobcrm.email.repository.EmailDraftRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailSenderService {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderService.class);

    private final JavaMailSender mailSender;
    private final EmailDraftRepository emailDraftRepository;
    private final AppProperties appProperties;

    public EmailSenderService(JavaMailSender mailSender,
                              EmailDraftRepository emailDraftRepository,
                              AppProperties appProperties) {
        this.mailSender = mailSender;
        this.emailDraftRepository = emailDraftRepository;
        this.appProperties = appProperties;
    }

    public EmailDraft send(EmailDraft draft, String recipientEmail) {
        enforceRateLimit(draft.getUser().getId());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(appProperties.getEmail().getFrom());
            message.setTo(recipientEmail);
            message.setSubject(draft.getSubject());
            message.setText(draft.getBody());

            mailSender.send(message);
            log.info("Email sent for draft: {}", draft.getId());

            draft.setStatus("SENT");
            draft.setSentAt(LocalDateTime.now());
            return emailDraftRepository.save(draft);

        } catch (Exception e) {
            log.error("Failed to send email for draft: {}", draft.getId(), e);
            draft.setStatus("FAILED");
            emailDraftRepository.save(draft);
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }

    private void enforceRateLimit(UUID userId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        long sentToday = emailDraftRepository.countByUserIdAndStatusAndSentAtAfter(
                userId, "SENT", startOfDay);

        int limit = appProperties.getEmail().getDailySendLimit();

        if (sentToday >= limit) {
            throw new RuntimeException(
                "Daily email limit of " + limit + " reached. Try again tomorrow.");
        }
    }
}