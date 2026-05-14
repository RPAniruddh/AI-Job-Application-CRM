package com.jobcrm.application.dto;

public class UpdateApplicationRequest {

	private String company;
	private String roleTitle;
	private String jobUrl;
	private String rawDescription;
	private String notes;
	private String appliedDate;

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