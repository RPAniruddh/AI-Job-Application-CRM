package com.jobcrm.application.controller;

import com.jobcrm.application.dto.ApplicationResponse;
import com.jobcrm.application.dto.CreateApplicationRequest;
import com.jobcrm.application.dto.UpdateApplicationRequest;
import com.jobcrm.application.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

	private final ApplicationService applicationService;

	public ApplicationController(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	@GetMapping
	public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
		return ResponseEntity.ok(applicationService.getAllApplications());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable UUID id) {
		return ResponseEntity.ok(applicationService.getApplicationById(id));
	}

	@PostMapping
	public ResponseEntity<ApplicationResponse> createApplication(@Valid @RequestBody CreateApplicationRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createApplication(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApplicationResponse> updateApplication(@PathVariable UUID id,
			@RequestBody UpdateApplicationRequest request) {
		return ResponseEntity.ok(applicationService.updateApplication(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteApplication(@PathVariable UUID id) {
		applicationService.deleteApplication(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/stage")
	public ResponseEntity<ApplicationResponse> updateStage(@PathVariable UUID id,
			@RequestBody Map<String, String> body) {
		String stage = body.get("stage");
		return ResponseEntity.ok(applicationService.updateStage(id, stage));
	}
}