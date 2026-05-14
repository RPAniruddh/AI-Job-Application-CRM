package com.jobcrm.workflow.model;

import com.jobcrm.application.model.JobApplication;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_tasks")
public class WorkflowTask {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    @Column(nullable = false)
    private String taskType;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime scheduledFor;

    private LocalDateTime executedAt;

    @Column(columnDefinition = "JSONB")
    private String payload;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    // ── Getters & Setters ─────────────────────────────────────────

    public UUID getId() { return id; }

    public JobApplication getApplication() { return application; }
    public void setApplication(JobApplication application) { this.application = application; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getScheduledFor() { return scheduledFor; }
    public void setScheduledFor(LocalDateTime scheduledFor) { this.scheduledFor = scheduledFor; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Builder ───────────────────────────────────────────────────

    protected WorkflowTask() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final WorkflowTask instance = new WorkflowTask();

        public Builder application(JobApplication app) { instance.application = app; return this; }
        public Builder taskType(WorkflowTaskType type) { instance.taskType = type.name(); return this; }
        public Builder scheduledFor(LocalDateTime time) { instance.scheduledFor = time; return this; }
        public Builder payload(String payload) { instance.payload = payload; return this; }
        public WorkflowTask build() { return instance; }
    }
}