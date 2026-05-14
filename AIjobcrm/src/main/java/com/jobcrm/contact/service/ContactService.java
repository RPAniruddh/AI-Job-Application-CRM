package com.jobcrm.contact.service;

import com.jobcrm.application.repository.JobApplicationRepository;
import com.jobcrm.auth.model.User;
import com.jobcrm.contact.dto.ContactResponse;
import com.jobcrm.contact.dto.CreateContactRequest;
import com.jobcrm.contact.dto.UpdateContactRequest;
import com.jobcrm.contact.model.Contact;
import com.jobcrm.contact.repository.ContactRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContactService {

	private final ContactRepository contactRepository;
	private final JobApplicationRepository applicationRepository;

	public ContactService(ContactRepository contactRepository, JobApplicationRepository applicationRepository) {
		this.contactRepository = contactRepository;
		this.applicationRepository = applicationRepository;
	}

	private User getCurrentUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public List<ContactResponse> getAllContacts() {
		UUID userId = getCurrentUser().getId();
		return contactRepository.findByUserId(userId).stream().map(ContactResponse::from).collect(Collectors.toList());
	}

	public ContactResponse getContactById(UUID id) {
		UUID userId = getCurrentUser().getId();
		Contact contact = contactRepository.findByIdAndUserId(id, userId)
				.orElseThrow(() -> new RuntimeException("Contact not found"));
		return ContactResponse.from(contact);
	}

	public ContactResponse createContact(CreateContactRequest request) {
		User user = getCurrentUser();

		Contact contact = new Contact();
		contact.setUser(user);
		contact.setName(request.getName());
		contact.setTitle(request.getTitle());
		contact.setEmail(request.getEmail());
		contact.setLinkedinUrl(request.getLinkedinUrl());
		contact.setNotes(request.getNotes());

		if (request.getApplicationId() != null) {
			applicationRepository.findByIdAndUserId(request.getApplicationId(), user.getId())
					.ifPresent(contact::setApplication);
		}

		return ContactResponse.from(contactRepository.save(contact));
	}

	public ContactResponse updateContact(UUID id, UpdateContactRequest request) {
		UUID userId = getCurrentUser().getId();
		Contact contact = contactRepository.findByIdAndUserId(id, userId)
				.orElseThrow(() -> new RuntimeException("Contact not found"));

		if (request.getName() != null)
			contact.setName(request.getName());
		if (request.getTitle() != null)
			contact.setTitle(request.getTitle());
		if (request.getEmail() != null)
			contact.setEmail(request.getEmail());
		if (request.getLinkedinUrl() != null)
			contact.setLinkedinUrl(request.getLinkedinUrl());
		if (request.getNotes() != null)
			contact.setNotes(request.getNotes());

		return ContactResponse.from(contactRepository.save(contact));
	}

	@Transactional
	public void deleteContact(UUID id) {
		UUID userId = getCurrentUser().getId();
		contactRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new RuntimeException("Contact not found"));
		contactRepository.deleteByIdAndUserId(id, userId);
	}
}