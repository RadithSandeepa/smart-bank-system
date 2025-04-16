package com.synapsecode.accountservice.repository;

import com.synapsecode.accountservice.entity.ContactDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContactDetailsRepository extends JpaRepository<ContactDetails, UUID> {
    Optional<ContactDetails> findByEmail(String email);

    Optional<ContactDetails> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}