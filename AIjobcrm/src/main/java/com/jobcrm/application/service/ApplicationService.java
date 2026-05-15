package com.jobcrm.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobcrm.application.dto.ApplicationResponse;
import com.jobcrm.application.dto.CreateApplicationRequest;
import com.jobcrm.application.dto.UpdateApplicationRequest;
import com.jobcrm.application.model.ApplicationStage;
import com.jobcrm.application.model.JobApplication;
import com.jobcrm.application.repository.JobApplicationRepository;
import com.jobcrm.auth.model.User;
import com.jobcrm.workflow.model.WorkflowTask;
import com.jobcrm.workflow.model.WorkflowTaskType;
import com.jobcrm.workflow.repository.WorkflowTaskRepository;

@Service
public class ApplicationService {

	private final JobApplicationRepository applicationRepository;
	private final WorkflowTaskRepository workflowTaskRepository;

	public ApplicationService(JobApplicationRepository applicationRepository,
			WorkflowTaskRepository workflowTaskRepository) {
		this.applicationRepository = applicationRepository;
		this.workflowTaskRepository = workflowTaskRepository;
	}

	// ── Helper — get authenticated user ──────────────────────────

	private User getCurrentUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	// ── CRUD Operations ───────────────────────────────────────────

	public List<ApplicationResponse> getAllApplications() {
		UUID userId = getCurrentUser().getId();
		return applicationRepository.findByUserId(userId).stream().map(ApplicationResponse::from)
				.collect(Collectors.toList());
	}

	public ApplicationResponse getApplicationById(UUID id) {
		UUID userId = getCurrentUser().getId();
		JobApplication application = applicationRepository.findByIdAndUserId(id, userId)
				.orElseThrow(() -> new RuntimeException("Application not found"));
		return ApplicationResponse.from(application);
	}

	public ApplicationResponse createApplication(CreateApplicationRequest request) {
		User user = getCurrentUser();

		JobApplication application = new JobApplication();
		application.setUser(user);
		application.setCompany(request.getCompany());
		application.setRoleTitle(request.getRoleTitle());
		application.setJobUrl(request.getJobUrl());
		application.setRawDescription(request.getRawDescription());
		application.setNotes(request.getNotes());

		if (request.getAppliedDate() != null && !request.getAppliedDate().isBlank()) {
			application.setAppliedDate(LocalDate.parse(request.getAppliedDate()));
		}

		JobApplication saved = applicationRepository.save(application);
		return ApplicationResponse.from(saved);
	}

	public ApplicationResponse updateApplication(UUID id, UpdateApplicationRequest request) {
		UUID userId = getCurrentUser().getId();

		JobApplication application = applicationRepository.findByIdAndUserId(id, userId)
				.orElseThrow(() -> new RuntimeException("Application not found"));

		if (request.getCompany() != null)
			application.setCompany(request.getCompany());
		if (request.getRoleTitle() != null)
			application.setRoleTitle(request.getRoleTitle());
		if (request.getJobUrl() != null)
			application.setJobUrl(request.getJobUrl());
		if (request.getRawDescription() != null)
			application.setRawDescription(request.getRawDescription());
		if (request.getNotes() != null)
			application.setNotes(request.getNotes());
		if (request.getAppliedDate() != null && !request.getAppliedDate().isBlank()) {
			application.setAppliedDate(LocalDate.parse(request.getAppliedDate()));
		}

		JobApplication saved = applicationRepository.save(application);
		return ApplicationResponse.from(saved);
	}

	@Transactional
	public void deleteApplication(UUID id) {
		UUID userId = getCurrentUser().getId();
		applicationRepository.findByIdAndUserId(id, userId)
				.orElseThrow(() -> new RuntimeException("Application not found"));
		applicationRepository.deleteByIdAndUserId(id, userId);
	}

	// ── Kanban Stage Transition ───────────────────────────────────

	public ApplicationResponse updateStage(UUID id, String stage) {
	    UUID userId = getCurrentUser().getId();

	    JobApplication application = applicationRepository
	            .findByIdAndUserId(id, userId)
	            .orElseThrow(() -> new RuntimeException("Application not found"));

	    ApplicationStage newStage;
	    try {
	        newStage = ApplicationStage.valueOf(stage.toUpperCase());
	    } catch (IllegalArgumentException e) {
	        throw new RuntimeException("Invalid stage: " + stage);
	    }

	    application.setStage(newStage);
	    JobApplication saved = applicationRepository.save(application);

	    // Trigger workflow tasks based on new stage
	    triggerWorkflowForStage(saved, newStage);

	    return ApplicationResponse.from(saved);
	}

	private void triggerWorkflowForStage(JobApplication application, ApplicationStage stage) {
	    switch (stage) {
	        case APPLIED:
	            // Schedule follow-up if no response in 7 days
	            WorkflowTask followUp = WorkflowTask.builder()
	                    .application(application)
	                    .taskType(WorkflowTaskType.SEND_FOLLOWUP)
	                    .scheduledFor(LocalDateTime.now().plusMinutes(2))
	                    .payload("{\"message\": \"Follow up on application\"}")
	                    .build();
	            workflowTaskRepository.save(followUp);

	            WorkflowTask confirmation = WorkflowTask.builder()
	                    .application(application)
	                    .taskType(WorkflowTaskType.SEND_CONFIRMATION)
	                    .scheduledFor(LocalDateTime.now().plusSeconds(10))
	                    .payload("{\"message\": \"Application confirmation\"}")
	                    .build();
	            workflowTaskRepository.save(confirmation);
	            break;

	        case INTERVIEWING:
	            // Schedule thank-you email reminder 24 hours after moving to interview
	            WorkflowTask thankYou = WorkflowTask.builder()
	                    .application(application)
	                    .taskType(WorkflowTaskType.SEND_THANKYOU)
	                    .scheduledFor(LocalDateTime.now().plusMinutes(2))
	                    .payload("{\"message\": \"Send thank-you email after interview\"}")
	                    .build();
	            workflowTaskRepository.save(thankYou);
	            break;

	        case REJECTED:
	        case WITHDRAWN:
	            // Cancel all pending tasks when application is closed
	            workflowTaskRepository.cancelPendingTasksForApplication(application.getId());
	            break;

	        default:
	            // No workflow trigger for other stages
	            break;
	    }
	}
}