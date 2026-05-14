package com.jobcrm.ai.repository;

import com.jobcrm.ai.model.UserResume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserResumeRepository extends JpaRepository<UserResume, UUID> {
    Optional<UserResume> findByUserId(UUID userId);
}