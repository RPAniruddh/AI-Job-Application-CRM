package com.jobcrm.application.repository;

import com.jobcrm.application.model.ApplicationStage;
import com.jobcrm.application.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

	List<JobApplication> findByUserId(UUID userId);

	List<JobApplication> findByUserIdAndStage(UUID userId, ApplicationStage stage);

	Optional<JobApplication> findByIdAndUserId(UUID id, UUID userId);

	void deleteByIdAndUserId(UUID id, UUID userId);

	long countByUserId(UUID userId);
}