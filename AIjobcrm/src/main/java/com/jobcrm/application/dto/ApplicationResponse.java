package com.jobcrm.application.dto;

import com.jobcrm.application.model.ApplicationStage;
import com.jobcrm.application.model.JobApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ApplicationResponse {

	private UUID id;
	private String company;
	private String roleTitle;
	private String jobUrl;
	private String rawDescription;
	private ApplicationStage stage;
	private Integer fitScore;
	private String notes;
	private LocalDate appliedDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// Static factory method — converts entity to DTO
	public static ApplicationResponse from(JobApplication app) {
		ApplicationResponse dto = new ApplicationResponse();
		dto.id = app.getId();
		dto.company = app.getCompany();
		dto.roleTitle = app.getRoleTitle();
		dto.jobUrl = app.getJobUrl();
		dto.rawDescription = app.getRawDescription();
		dto.stage = app.getStage();
		dto.fitScore = app.getFitScore();
		dto.notes = app.getNotes();
		dto.appliedDate = app.getAppliedDate();
		dto.createdAt = app.getCreatedAt();
		dto.updatedAt = app.getUpdatedAt();
		return dto;
	}

	public UUID getId() {
		return id;
	}

	public String getCompany() {
		return company;
	}

	public String getRoleTitle() {
		return roleTitle;
	}

	public String getJobUrl() {
		return jobUrl;
	}

	public String getRawDescription() {
		return rawDescription;
	}

	public ApplicationStage getStage() {
		return stage;
	}

	public Integer getFitScore() {
		return fitScore;
	}

	public String getNotes() {
		return notes;
	}

	public LocalDate getAppliedDate() {
		return appliedDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}