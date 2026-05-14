package com.jobcrm.contact.dto;

import com.jobcrm.contact.model.Contact;
import java.time.LocalDateTime;
import java.util.UUID;

public class ContactResponse {

	private UUID id;
	private UUID applicationId;
	private String name;
	private String title;
	private String email;
	private String linkedinUrl;
	private String notes;
	private LocalDateTime createdAt;

	public static ContactResponse from(Contact contact) {
		ContactResponse dto = new ContactResponse();
		dto.id = contact.getId();
		dto.applicationId = contact.getApplication() != null ? contact.getApplication().getId() : null;
		dto.name = contact.getName();
		dto.title = contact.getTitle();
		dto.email = contact.getEmail();
		dto.linkedinUrl = contact.getLinkedinUrl();
		dto.notes = contact.getNotes();
		dto.createdAt = contact.getCreatedAt();
		return dto;
	}

	public UUID getId() {
		return id;
	}

	public UUID getApplicationId() {
		return applicationId;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public String getEmail() {
		return email;
	}

	public String getLinkedinUrl() {
		return linkedinUrl;
	}

	public String getNotes() {
		return notes;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}