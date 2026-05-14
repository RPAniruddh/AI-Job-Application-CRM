package com.jobcrm.application.model;

import com.jobcrm.auth.model.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_applications")
public class JobApplication {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)	
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String company;

	@Column(nullable = false)
	private String roleTitle;

	private String jobUrl;

	@Column(columnDefinition = "TEXT")
	private String rawDescription;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ApplicationStage stage = ApplicationStage.WISHLIST;

	private Integer fitScore;

	@Column(columnDefinition = "TEXT")
	private String notes;

	private LocalDate appliedDate;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	// ── Getters & Setters ─────────────────────────────────────────

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getRoleTitle() {
		return roleTitle;
	}

	public void setRoleTitle(String roleTitle) {
		this.roleTitle = roleTitle;
	}

	public String getJobUrl() {
		return jobUrl;
	}

	public void setJobUrl(String jobUrl) {
		this.jobUrl = jobUrl;
	}

	public String getRawDescription() {
		return rawDescription;
	}

	public void setRawDescription(String rawDescription) {
		this.rawDescription = rawDescription;
	}

	public ApplicationStage getStage() {
		return stage;
	}

	public void setStage(ApplicationStage stage) {
		this.stage = stage;
	}

	public Integer getFitScore() {
		return fitScore;
	}

	public void setFitScore(Integer fitScore) {
		this.fitScore = fitScore;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public LocalDate getAppliedDate() {
		return appliedDate;
	}

	public void setAppliedDate(LocalDate appliedDate) {
		this.appliedDate = appliedDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}