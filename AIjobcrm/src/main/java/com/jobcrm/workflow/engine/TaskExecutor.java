package com.jobcrm.workflow.engine;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jobcrm.email.model.EmailDraft;
import com.jobcrm.email.service.EmailDraftingService;
import com.jobcrm.workflow.model.WorkflowTask;
import com.jobcrm.workflow.model.WorkflowTaskType;
import com.jobcrm.workflow.repository.WorkflowTaskRepository;

@Component
public class TaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(TaskExecutor.class);

    private final EmailDraftingService emailDraftingService;
    private final WorkflowTaskRepository workflowTaskRepository;

    public TaskExecutor(EmailDraftingService emailDraftingService,
                        WorkflowTaskRepository workflowTaskRepository) {
        this.emailDraftingService = emailDraftingService;
        this.workflowTaskRepository = workflowTaskRepository;
    }

    public void execute(WorkflowTask task) {
        log.info("TASK EXECUTOR: Executing task {} of type {}", task.getId(), task.getTaskType());

        try {
        	if (task.getTaskType().equals(WorkflowTaskType.SEND_FOLLOWUP.name())) {
        		 EmailDraft draft = emailDraftingService.draftFollowUpEmail(task.getApplication());
        		    log.info("DRAFT GENERATED:\nSubject: {}\nBody:\n{}", draft.getSubject(), draft.getBody());



        	} else if (task.getTaskType().equals(WorkflowTaskType.SEND_THANKYOU.name())) {
        		EmailDraft draft = emailDraftingService.draftThankYouEmail(task.getApplication());
        	    log.info("DRAFT GENERATED:\nSubject: {}\nBody:\n{}", draft.getSubject(), draft.getBody());

        	} else if (task.getTaskType().equals(WorkflowTaskType.CHECK_STATUS.name())) {
        	    log.info("TASK EXECUTOR: Check status reminder for application {}",
        	            task.getApplication().getId());

        	} else if (task.getTaskType().equals(WorkflowTaskType.SCHEDULE_REMINDER.name())) {
        	    log.info("TASK EXECUTOR: Reminder — payload: {}", task.getPayload());
        	}

            task.setStatus("EXECUTED");
            task.setExecutedAt(LocalDateTime.now());
            workflowTaskRepository.save(task);
            log.info("TASK EXECUTOR: Task {} marked EXECUTED", task.getId());

        } catch (Exception e) {
            log.error("TASK EXECUTOR: Task {} failed — {}", task.getId(), e.getMessage(), e);
            task.setStatus("FAILED");
            workflowTaskRepository.save(task);
        }
    }
}