package com.jobcrm.workflow.model;

public enum WorkflowTaskType {
    SEND_FOLLOWUP,      // Follow-up email after no response
    SEND_THANKYOU,      // Thank-you email after interview
    CHECK_STATUS,       // Reminder to check application status
    SCHEDULE_REMINDER   // Generic reminder for user
}