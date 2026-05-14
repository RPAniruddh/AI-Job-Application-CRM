package com.jobcrm.workflow.repository;

import com.jobcrm.workflow.model.WorkflowTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WorkflowTaskRepository extends JpaRepository<WorkflowTask, UUID> {

    // The core scheduler query — find all tasks due for execution
	@Query("SELECT t FROM WorkflowTask t JOIN FETCH t.application WHERE t.status = 'PENDING' AND t.scheduledFor <= :now")
	List<WorkflowTask> findPendingTasksDue(LocalDateTime now);

    // Cancel all pending tasks for an application
    @Query("UPDATE WorkflowTask t SET t.status = 'CANCELLED' WHERE t.application.id = :applicationId AND t.status = 'PENDING'")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void cancelPendingTasksForApplication(@Param("applicationId") UUID applicationId);

    List<WorkflowTask> findByApplicationId(UUID applicationId);
}