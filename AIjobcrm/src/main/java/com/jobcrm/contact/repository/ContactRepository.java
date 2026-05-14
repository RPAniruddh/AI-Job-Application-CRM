package com.jobcrm.contact.repository;

import com.jobcrm.contact.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

	List<Contact> findByUserId(UUID userId);

	List<Contact> findByUserIdAndApplicationId(UUID userId, UUID applicationId);

	Optional<Contact> findByIdAndUserId(UUID id, UUID userId);

	void deleteByIdAndUserId(UUID id, UUID userId);
}