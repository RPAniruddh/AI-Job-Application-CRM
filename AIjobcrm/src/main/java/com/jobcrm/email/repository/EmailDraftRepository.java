package com.jobcrm.email.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jobcrm.email.model.EmailDraft;

@Repository
public interface EmailDraftRepository extends JpaRepository<EmailDraft, UUID> {

	List<EmailDraft> findByApplicationIdAndUserId(UUID applicationId, UUID userId);

	@Query("SELECT d FROM EmailDraft d JOIN FETCH d.application JOIN FETCH d.user WHERE d.user.id = :userId ORDER BY d.createdAt DESC")
	List<EmailDraft> findByUserIdOrderByCreatedAtDesc(UUID userId);


	@Query("SELECT d FROM EmailDraft d JOIN FETCH d.application JOIN FETCH d.user WHERE d.id = :id AND d.user.id = :userId")
	Optional<EmailDraft> findByIdAndUserId(UUID id, UUID userId);

	long countByUserIdAndStatusAndSentAtAfter(UUID userId, String status, java.time.LocalDateTime after);
}