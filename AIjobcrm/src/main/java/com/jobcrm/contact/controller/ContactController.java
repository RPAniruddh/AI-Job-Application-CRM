package com.jobcrm.contact.controller;

import com.jobcrm.contact.dto.ContactResponse;
import com.jobcrm.contact.dto.CreateContactRequest;
import com.jobcrm.contact.dto.UpdateContactRequest;
import com.jobcrm.contact.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

	private final ContactService contactService;

	public ContactController(ContactService contactService) {
		this.contactService = contactService;
	}

	@GetMapping
	public ResponseEntity<List<ContactResponse>> getAllContacts() {
		return ResponseEntity.ok(contactService.getAllContacts());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ContactResponse> getContactById(@PathVariable UUID id) {
		return ResponseEntity.ok(contactService.getContactById(id));
	}

	@PostMapping
	public ResponseEntity<ContactResponse> createContact(@Valid @RequestBody CreateContactRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(contactService.createContact(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ContactResponse> updateContact(@PathVariable UUID id,
			@RequestBody UpdateContactRequest request) {
		return ResponseEntity.ok(contactService.updateContact(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteContact(@PathVariable UUID id) {
		contactService.deleteContact(id);
		return ResponseEntity.noContent().build();
	}
}