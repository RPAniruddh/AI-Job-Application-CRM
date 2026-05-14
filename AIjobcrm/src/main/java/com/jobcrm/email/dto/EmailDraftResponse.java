package com.jobcrm.email.dto;

import com.jobcrm.email.model.EmailDraft;

import java.time.LocalDateTime;
import java.util.UUID;

public class EmailDraftResponse {

    private UUID id;
    private UUID applicationId;
    private String company;
    private String roleTitle;
    private String subject;
    private String body;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    private EmailDraftResponse() {}

    public static EmailDraftResponse from(EmailDraft draft) {
        EmailDraftResponse response = new EmailDraftResponse();
        response.id = draft.getId();
        response.applicationId = draft.getApplication().getId();
        response.company = draft.getApplication().getCompany();
        response.roleTitle = draft.getApplication().getRoleTitle();
        response.subject = draft.getSubject();
        response.body = draft.getBody();
        response.status = draft.getStatus();
        response.createdAt = draft.getCreatedAt();
        response.sentAt = draft.getSentAt();
        return response;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getApplicationId() { return applicationId; }
    public String getCompany() { return company; }
    public String getRoleTitle() { return roleTitle; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getSentAt() { return sentAt; }
}