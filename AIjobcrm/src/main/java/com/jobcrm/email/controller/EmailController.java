package com.jobcrm.email.controller;

import com.jobcrm.auth.model.User;
import com.jobcrm.email.dto.EmailDraftResponse;
import com.jobcrm.email.model.EmailDraft;
import com.jobcrm.email.repository.EmailDraftRepository;
import com.jobcrm.email.service.EmailSenderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailDraftRepository emailDraftRepository;
    private final EmailSenderService emailSenderService;

    public EmailController(EmailDraftRepository emailDraftRepository,
                           EmailSenderService emailSenderService) {
        this.emailDraftRepository = emailDraftRepository;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping
    public ResponseEntity<List<EmailDraftResponse>> getAllDrafts() {
        User currentUser = getCurrentUser();

        List<EmailDraftResponse> drafts = emailDraftRepository
                .findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(EmailDraftResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(drafts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailDraftResponse> getDraft(@PathVariable UUID id) {
        User currentUser = getCurrentUser();

        EmailDraft draft = emailDraftRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Draft not found"));

        return ResponseEntity.ok(EmailDraftResponse.from(draft));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmailDraftResponse> updateDraft(
            @PathVariable UUID id,
            @RequestBody UpdateDraftRequest request) {

        User currentUser = getCurrentUser();

        EmailDraft draft = emailDraftRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Draft not found"));

        if (!"DRAFT".equals(draft.getStatus())) {
            return ResponseEntity.badRequest().build();
        }

        if (request.getSubject() != null) draft.setSubject(request.getSubject());
        if (request.getBody() != null) draft.setBody(request.getBody());

        emailDraftRepository.save(draft);
        return ResponseEntity.ok(EmailDraftResponse.from(draft));
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<EmailDraftResponse> sendDraft(
            @PathVariable UUID id,
            @RequestBody SendDraftRequest request) {

        User currentUser = getCurrentUser();

        EmailDraft draft = emailDraftRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Draft not found"));

        if (!"DRAFT".equals(draft.getStatus())) {
            return ResponseEntity.badRequest().build();
        }

        EmailDraft sent = emailSenderService.send(draft, request.getRecipientEmail());
        return ResponseEntity.ok(EmailDraftResponse.from(sent));
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    // ── Inner request classes ─────────────────────────────────────

    public static class SendDraftRequest {
        private String recipientEmail;
        public String getRecipientEmail() { return recipientEmail; }
        public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    }

    public static class UpdateDraftRequest {
        private String subject;
        private String body;
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
    }
}