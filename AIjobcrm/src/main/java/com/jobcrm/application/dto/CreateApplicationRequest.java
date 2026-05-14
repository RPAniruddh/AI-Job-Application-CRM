package com.jobcrm.application.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateApplicationRequest {

	@NotBlank(message = "Company is required")
	private String company;

	@NotBlank(message = "Role title is required")
	private String roleTitle;

	private String jobUrl;
	private String rawDescription;
	private String notes;
	private String appliedDate; // ISO format: "2024-01-15"

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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getAppliedDate() {
		return appliedDate;
	}

	public void setAppliedDate(String appliedDate) {
		this.appliedDate = appliedDate;
	}

}