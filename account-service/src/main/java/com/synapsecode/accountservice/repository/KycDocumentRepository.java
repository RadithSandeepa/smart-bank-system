package com.synapsecode.accountservice.repository;

import com.synapsecode.accountservice.entity.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface KycDocumentRepository extends JpaRepository<KycDocument, UUID> {
    List<KycDocument> findByAccountId(UUID accountId);

//    List<KycDocument> findByAccountIdAndDocumentType(UUID accountId, DocumentType documentType);

    List<KycDocument> findByExpiryDateBefore(LocalDate date);

    void deleteByAccountId(UUID accountId);
}