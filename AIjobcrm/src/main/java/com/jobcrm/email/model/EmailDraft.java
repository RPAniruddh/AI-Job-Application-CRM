package com.jobcrm.email.model;

import com.jobcrm.application.model.JobApplication;
import com.jobcrm.auth.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_drafts")
public class EmailDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    protected EmailDraft() {}

    private EmailDraft(Builder builder) {
        this.user = builder.user;
        this.application = builder.application;
        this.subject = builder.subject;
        this.body = builder.body;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
    }

    // Getters
    public UUID getId() { return id; }
    public User getUser() { return user; }
    public JobApplication getApplication() { return application; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getSentAt() { return sentAt; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setBody(String body) { this.body = body; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user;
        private JobApplication application;
        private String subject;
        private String body;
        private String status = "DRAFT";
        private LocalDateTime createdAt = LocalDateTime.now();

        public Builder user(User user) { this.user = user; return this; }
        public Builder application(JobApplication application) { this.application = application; return this; }
        public Builder subject(String subject) { this.subject = subject; return this; }
        public Builder body(String body) { this.body = body; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public EmailDraft build() { return new EmailDraft(this); }
    }
}