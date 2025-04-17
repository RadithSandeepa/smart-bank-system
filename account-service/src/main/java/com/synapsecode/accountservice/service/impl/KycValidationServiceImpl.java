//package com.synapsecode.accountservice.service.impl;
//
//
//import com.synapsecode.accountservice.entity.Account;
//import com.synapsecode.accountservice.entity.KycDocument;
//import com.synapsecode.accountservice.entity.KycValidationResult;
//import com.synapsecode.accountservice.entity.KycValidationStatus;
//import com.synapsecode.accountservice.event.KycValidationEvent;
//import com.synapsecode.accountservice.exception.AccountNotFoundException;
//import com.synapsecode.accountservice.exception.KycValidationException;
//import com.synapsecode.accountservice.repository.AccountRepository;
//import com.synapsecode.accountservice.repository.KycDocumentRepository;
//import com.synapsecode.accountservice.service.AuditService;
//import com.synapsecode.accountservice.service.KycValidationService;
//import com.synapsecode.accountservice.service.NotificationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class KycValidationServiceImpl implements KycValidationService {
//
//    private static final Logger logger = LoggerFactory.getLogger(KycValidationServiceImpl.class);
//
//    private static final Pattern EMAIL_PATTERN =
//            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
//    private static final Pattern PHONE_PATTERN =
//            Pattern.compile("^\\+?[0-9]{10,15}$");
//    private final AccountRepository accountRepository;
//    private final KycDocumentRepository kycDocumentRepository;
//    private final AuditService auditService;
//    private final ApplicationEventPublisher eventPublisher;
//    private final NotificationService notificationService;
//
//    public void validateKycDocument(KycDocument kycDocument) {
//        log.info("Validating KYC documents");
//
//        if (kycDocument == null) {
//            throw new KycValidationException("KYC documents are required");
//        }
//
//        boolean hasIdentityProof = false;
//        boolean hasAddressProof = false;
//
////        for (KycDocument document : documents) {
////            validateDocumentExpiry(document);
////
////            switch (document.getDocumentType()) {
////                case NATIONAL_ID:
////                    hasIdentityProof = true;
////                    break;
////                case PASSPORT:
////                    hasAddressProof = true;
////                    break;
////            }
////        }
//        validateDocumentExpiry(kycDocument);
//
//        switch (kycDocument.getDocumentType()) {
//            case NATIONAL_ID:
//                hasIdentityProof = true;
//                break;
//            case PASSPORT:
//                hasAddressProof = true;
//                break;
//        }
//
//        if (!hasIdentityProof) {
//            throw new KycValidationException("Identity proof document is required");
//        }
//
//        if (!hasAddressProof) {
//            throw new KycValidationException("Address proof document is required");
//        }
//
//        log.info("KYC validation successful");
//    }
//
//    @Override
//    public void validateDocumentExpiry(KycDocument document) {
//        if (document.getExpiryDate() != null && document.getExpiryDate().isBefore(LocalDate.now())) {
//            throw new KycValidationException("Document " + document.getDocumentNumber() +
//                    " of type " + document.getDocumentType() + " is expired");
//        }
//
//        if (document.getDocumentNumber() == null || document.getDocumentNumber().isEmpty()) {
//            throw new KycValidationException("Document number is required");
//        }
//    }
//
//    @Override
//    public void validateEmail(String email) {
//        if (email == null || email.isEmpty()) {
//            throw new KycValidationException("Email is required");
//        }
//
//        if (!EMAIL_PATTERN.matcher(email).matches()) {
//            throw new KycValidationException("Invalid email format");
//        }
//    }
//
//    @Override
//    public void validatePhoneNumber(String phoneNumber) {
//        if (phoneNumber == null || phoneNumber.isEmpty()) {
//            throw new KycValidationException("Phone number is required");
//        }
//
//        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
//            throw new KycValidationException("Invalid phone number format");
//        }
//    }
//
//    @Override
//    public void validateAddress(String addressLine1, String city, String state, String country, String postalCode) {
//        if (addressLine1 == null || addressLine1.isEmpty()) {
//            throw new KycValidationException("Address line 1 is required");
//        }
//
//        if (city == null || city.isEmpty()) {
//            throw new KycValidationException("City is required");
//        }
//
//        if (state == null || state.isEmpty()) {
//            throw new KycValidationException("State is required");
//        }
//
//        if (country == null || country.isEmpty()) {
//            throw new KycValidationException("Country is required");
//        }
//
//        if (postalCode == null || postalCode.isEmpty()) {
//            throw new KycValidationException("Postal code is required");
//        }
//    }
//
//    @Override
//    @Async("kycValidationTaskExecutor")
//    public void startValidation(UUID accountId) {
//        logger.info("Starting asynchronous KYC validation for account: {}", accountId);
//
//        // Get account or throw exception if not found
//        Account account = accountRepository.findById(accountId)
//                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
//
//        // Create or update validation status entry
//        List<KycDocument> kycDocument = kycDocumentRepository.findByAccountId(accountId);
//        if (kycDocument.isEmpty()) {
//            throw new KycValidationException("No KYC documents found for account: " + accountId);
//        }
//        kycDocument.forEach(document -> {
//            document.setVerificationStatus("IN_PROGRESS");
//            document.setVerificationComments("KYC validation started");
//            document.setVerificationDate(LocalDateTime.now());
//            kycDocumentRepository.save(document);
//        });
//
//        // Publish event indicating start of validation
//        eventPublisher.publishEvent(KycValidationEvent.builder()
//                .accountId(accountId)
//                .eventType("START")
//                .status("IN_PROGRESS")
//                .message("KYC validation process started")
//                .build());
//
//        try {
//            // Get all documents for the account
////            List<KycDocument> documents = kycDocumentRepository.findByAccountId(accountId);
////
////            if (documents.isEmpty()) {
////                // No documents found, update status
////                updateValidationStatus(accountId, "FAILED", "No KYC documents found for validation");
////                return;
////            }
////
////            // Create validation result object
////            KycValidationResult validationResult = KycValidationResult.builder()
////                    .accountId(accountId)
////                    .validationStartTime(LocalDateTime.now())
////                    .build();
////
////            // Process each document with delay to simulate external API calls
////            List<KycValidationResult.DocumentValidationResult> documentResults = documents.stream()
////                    .map(document -> validateDocumentWithExternalServices(document))
////                    .collect(Collectors.toList());
////
////            validationResult.setDocumentResults(documentResults);
////
////            // Check if all documents are valid
////            boolean allValid = documentResults.stream().allMatch(DocumentValidationResult::isValid);
////
////            // Set overall status
////            if (allValid) {
////                validationResult.setOverallStatus("COMPLETE");
////                validationResult.setValidationSummary("All KYC documents have been successfully validated");
////            } else {
////                validationResult.setOverallStatus("REJECTED");
////                validationResult.setValidationSummary("One or more KYC documents failed validation");
////            }
////
////            validationResult.setValidationEndTime(LocalDateTime.now());
//
//            List<KycValidationResult.DocumentValidationResult> documentResults = kycDocument.stream()
//                    .map(this::validateDocumentWithExternalServices)
//                    .collect(Collectors.toList());
//
//            // Check if all documents are valid
//            boolean allValid = documentResults.stream().allMatch(KycValidationResult.DocumentValidationResult::isValid);
//
//
//            // Update document verification status in database
//            updateDocumentsVerificationStatus(documentResults);
//
//            // Update validation status
////            updateValidationStatus(accountId, validationResult.getOverallStatus(), validationResult.getValidationSummary());
//
//            // Audit the validation
//            auditService.recordKycUpdate(account);
//
//            String overallStatus = allValid ? "COMPLETE" : "FAILED";
//            String message = allValid ? "KYC validation completed successfully" : "Some documents failed validation";
//            eventPublisher.publishEvent(KycValidationEvent.builder()
//                    .accountId(accountId)
//                    .eventType("COMPLETE")
//                    .status(overallStatus)
//                    .message(message)
//                    .build());
//
//            // Send notification to customer
////            notificationService.sendKycStatusNotification(account, validationResult.getOverallStatus());
//
//            logger.info("KYC validation completed for account: {} with status: {}", accountId, overallStatus);
//
//
//        } catch (Exception e) {
//            logger.error("Error during KYC validation for account: {}", accountId, e);
//            updateValidationStatus(accountId, "FAILED", "Error during validation: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Simulates validation with external services like identity verification APIs,
//     * document authenticity checking, etc.
//     */
//    private KycValidationResult.DocumentValidationResult validateDocumentWithExternalServices(KycDocument document) {
//        logger.info("Validating document: {} of type: {}", document.getId(), document.getDocumentType());
//
//        KycValidationResult.DocumentValidationResult result = new KycValidationResult.DocumentValidationResult();
//        result.setDocumentId(document.getId());
//        result.setDocumentType(document.getDocumentType());
//        result.setDocumentNumber(document.getDocumentNumber());
//
//        try {
//            // Simulate API call delay
//            Thread.sleep(1000);
//
//            // Basic validation first
//            validateKycDocument(document);
//
//            // Simulate external validation logic based on document type
//            boolean isValid = simulateExternalValidation(document);
//
//            result.setValid(isValid);
//            if (isValid) {
//                result.setValidationMessage("Document successfully validated");
//            } else {
//                result.setValidationMessage("Document failed external validation checks");
//            }
//
//        } catch (KycValidationException e) {
//            result.setValid(false);
//            result.setValidationMessage(e.getMessage());
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            result.setValid(false);
//            result.setValidationMessage("Validation process was interrupted");
//        } catch (Exception e) {
//            result.setValid(false);
//            result.setValidationMessage("Unexpected error during validation: " + e.getMessage());
//        }
//
//        return result;
//    }
//
//    /**
//     * Simulates calls to external validation services
//     */
//    private boolean simulateExternalValidation(KycDocument document) {
//        // In a real implementation, this would make calls to external APIs
//        // For demonstration, we'll implement a simple simulation
//
//        switch (document.getDocumentType()) {
//            case PASSPORT:
//                // Simulate 90% success rate for passports
//                return Math.random() < 0.9;
//
//            case DRIVERS_LICENSE:
//                // Simulate 85% success rate for driver's licenses
//                return Math.random() < 0.85;
//
//            case NATIONAL_ID:
//                // Simulate 95% success rate for national IDs
//                return Math.random() < 0.95;
//
//            case TAX_ID:
//                // Simulate 98% success rate for tax IDs
//                return Math.random() < 0.98;
//
//            case UTILITY_BILL:
//                // Simulate 80% success rate for utility bills
//                return Math.random() < 0.8;
//
//            default:
//                // For unknown document types, default to 50% success rate
//                return Math.random() < 0.5;
//        }
//    }
//
//    private void updateDocumentsVerificationStatus(List<KycValidationResult.DocumentValidationResult> documentResults) {
//        for (KycValidationResult.DocumentValidationResult result : documentResults) {
//            Optional<KycDocument> documentOpt = kycDocumentRepository.findById(result.getDocumentId());
//
//            if (documentOpt.isPresent()) {
//                KycDocument document = documentOpt.get();
//                document.setVerificationStatus(result.isValid() ? "VERIFIED" : "REJECTED");
//                document.setVerificationComments(result.getValidationMessage());
//                document.setVerificationDate(LocalDateTime.now());
//                kycDocumentRepository.save(document);
//            }
//        }
//    }
//
//    private void updateValidationStatus(UUID accountId, String status, String message) {
//
//        List<KycDocument> kycDocument = kycDocumentRepository.findByAccountId(accountId);
////                .orElse(new KycValidationStatus());
//
//        for (k : kycDocument){
//
//        }
//        kycDocument.setStatus(status);
//        kycDocument.setMessage(message);
//        kycDocument.setLastUpdated(LocalDateTime.now());
//
//        if ("COMPLETE".equals(status) || "REJECTED".equals(status) || "FAILED".equals(status)) {
//            kycDocument.setEndTime(LocalDateTime.now());
//        }
//
//        validationStatusRepository.save(kycDocument);
//
//        // Publish event for status change
//        eventPublisher.publishEvent(KycValidationEvent.builder()
//                .accountId(accountId)
//                .eventType(status.equals("FAILED") ? "FAILED" : "COMPLETE")
//                .status(status)
//                .message(message)
//                .build());
//    }
//
//    // Create a method to get the latest validation status for an account
////    public KycValidationStatus getValidationStatus(UUID accountId) {
////        List<KycDocument> kycDocument = kycDocumentRepository.findByAccountId(accountId);
////        if (kycDocument.isEmpty()){
////            throw new  KycValidationException("No validation record found for account: " + accountId);
////        }
////        return kycDocument;
////    }
//}

package com.synapsecode.accountservice.service.impl;

import com.synapsecode.accountservice.entity.Account;
import com.synapsecode.accountservice.entity.DocumentType;
import com.synapsecode.accountservice.entity.KycDocument;
import com.synapsecode.accountservice.event.KycValidationEvent;
import com.synapsecode.accountservice.exception.AccountNotFoundException;
import com.synapsecode.accountservice.exception.KycValidationException;
import com.synapsecode.accountservice.repository.AccountRepository;
import com.synapsecode.accountservice.repository.KycDocumentRepository;
import com.synapsecode.accountservice.service.AuditService;
import com.synapsecode.accountservice.service.KycValidationService;
import com.synapsecode.accountservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class KycValidationServiceImpl implements KycValidationService {

    private static final Logger logger = LoggerFactory.getLogger(KycValidationServiceImpl.class);

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{10,15}$");
    private final AccountRepository accountRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;

    @Override
    public void validateKycDocument(KycDocument kycDocument) {
        log.info("Validating KYC document: {}", kycDocument.getDocumentNumber());

        if (kycDocument == null) {
            throw new KycValidationException("KYC document is required");
        }

        validateDocumentExpiry(kycDocument);

        // Validate document number
        if (kycDocument.getDocumentNumber() == null || kycDocument.getDocumentNumber().isEmpty()) {
            throw new KycValidationException("Document number is required");
        }

        log.info("KYC document validation successful for document: {}", kycDocument.getDocumentNumber());
    }

    @Override
    public void validateDocumentExpiry(KycDocument document) {
        if (document.getExpiryDate() != null && document.getExpiryDate().isBefore(LocalDate.now())) {
            throw new KycValidationException("Document " + document.getDocumentNumber() +
                    " of type " + document.getDocumentType() + " is expired");
        }
    }

    @Override
    public void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new KycValidationException("Email is required");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new KycValidationException("Invalid email format");
        }
    }

    @Override
    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new KycValidationException("Phone number is required");
        }

        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new KycValidationException("Invalid phone number format");
        }
    }

    @Override
    public void validateAddress(String addressLine1, String city, String state, String country, String postalCode) {
        if (addressLine1 == null || addressLine1.isEmpty()) {
            throw new KycValidationException("Address line 1 is required");
        }

        if (city == null || city.isEmpty()) {
            throw new KycValidationException("City is required");
        }

        if (state == null || state.isEmpty()) {
            throw new KycValidationException("State is required");
        }

        if (country == null || country.isEmpty()) {
            throw new KycValidationException("Country is required");
        }

        if (postalCode == null || postalCode.isEmpty()) {
            throw new KycValidationException("Postal code is required");
        }
    }

    @Override
    @Async("kycValidationTaskExecutor")
    public void startValidation(UUID accountId) {
        logger.info("Starting asynchronous KYC validation for account: {}", accountId);

        // Get account or throw exception if not found
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        // Get all documents for the account
        List<KycDocument> kycDocuments = kycDocumentRepository.findByAccountId(accountId);
        if (kycDocuments.isEmpty()) {
            throw new KycValidationException("No KYC documents found for account: " + accountId);
        }

        // Update status of all documents to in-progress
        for (KycDocument document : kycDocuments) {
            document.setVerificationStatus("IN_PROGRESS");
            document.setVerificationComments("KYC validation started");
            document.setVerificationDate(LocalDateTime.now());
            kycDocumentRepository.save(document);
        }

        // Publish event indicating start of validation
        eventPublisher.publishEvent(KycValidationEvent.builder()
                .accountId(accountId)
                .eventType("START")
                .status("IN_PROGRESS")
                .message("KYC validation process started")
                .build());

        try {
            // Check if required document types are present
            boolean hasIdentityProof = kycDocuments.stream()
                    .anyMatch(doc -> doc.getDocumentType() == DocumentType.NATIONAL_ID);
            boolean hasAddressProof = kycDocuments.stream()
                    .anyMatch(doc -> doc.getDocumentType() == DocumentType.PASSPORT);

            if (!hasIdentityProof) {
                publishValidationFailure(accountId, "Identity proof document is required");
                return;
            }

            if (!hasAddressProof) {
                publishValidationFailure(accountId, "Address proof document is required");
                return;
            }

            // Process each document and update its status
            boolean allValid = true;
            for (KycDocument document : kycDocuments) {
                try {
                    // Basic validation
                    validateKycDocument(document);

                    // Simulate external validation
                    boolean isValid = simulateExternalValidation(document);

                    if (isValid) {
                        document.setVerificationStatus("VERIFIED");
                        document.setVerificationComments("Document successfully validated");
                    } else {
                        document.setVerificationStatus("REJECTED");
                        document.setVerificationComments("Document failed external validation checks");
                        allValid = false;
                    }
                } catch (KycValidationException e) {
                    document.setVerificationStatus("REJECTED");
                    document.setVerificationComments(e.getMessage());
                    allValid = false;
                }

                document.setVerificationDate(LocalDateTime.now());
                document.setVerifiedBy("SYSTEM");
                kycDocumentRepository.save(document);
            }

            // Audit the validation
            auditService.recordKycUpdate(account);

            // Publish completion event
            String overallStatus = allValid ? "COMPLETE" : "FAILED";
            String message = allValid ? "KYC validation completed successfully" : "Some documents failed validation";

            eventPublisher.publishEvent(KycValidationEvent.builder()
                    .accountId(accountId)
                    .eventType("COMPLETE")
                    .status(overallStatus)
                    .message(message)
                    .build());

            // Send notification to customer
            notificationService.sendKycUpdateNotification(account);

            logger.info("KYC validation completed for account: {} with status: {}", accountId, overallStatus);

        } catch (Exception e) {
            logger.error("Error during KYC validation for account: {}", accountId, e);
            publishValidationFailure(accountId, "Error during validation: " + e.getMessage());
        }
    }

    /**
     * Simulates calls to external validation services
     */
    private boolean simulateExternalValidation(KycDocument document) {
        // In a real implementation, this would make calls to external APIs
        // For demonstration, we'll implement a simple simulation
        try {
            // Simulate API call delay
            Thread.sleep(1000);

            switch (document.getDocumentType()) {
                case PASSPORT:
                    // Simulate 90% success rate for passports
                    return Math.random() < 0.9;
                case DRIVERS_LICENSE:
                    // Simulate 85% success rate for driver's licenses
                    return Math.random() < 0.85;
                case NATIONAL_ID:
                    // Simulate 95% success rate for national IDs
                    return Math.random() < 0.95;
                case TAX_ID:
                    // Simulate 98% success rate for tax IDs
                    return Math.random() < 0.98;
                case UTILITY_BILL:
                    // Simulate 80% success rate for utility bills
                    return Math.random() < 0.8;
                default:
                    // For unknown document types, default to 50% success rate
                    return Math.random() < 0.5;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private void publishValidationFailure(UUID accountId, String message) {
        // Update all documents for this account to FAILED status
        List<KycDocument> kycDocuments = kycDocumentRepository.findByAccountId(accountId);
        for (KycDocument document : kycDocuments) {
            document.setVerificationStatus("FAILED");
            document.setVerificationComments(message);
            document.setVerificationDate(LocalDateTime.now());
            kycDocumentRepository.save(document);
        }

        // Publish failure event
        eventPublisher.publishEvent(KycValidationEvent.builder()
                .accountId(accountId)
                .eventType("FAILED")
                .status("FAILED")
                .message(message)
                .build());
    }

    /**
     * Get the overall KYC verification status for an account
     */
    public String getKycVerificationStatus(UUID accountId) {
        List<KycDocument> documents = kycDocumentRepository.findByAccountId(accountId);
        if (documents.isEmpty()) {
            return "NO_DOCUMENTS";
        }

        boolean hasRejected = documents.stream().anyMatch(doc -> "REJECTED".equals(doc.getVerificationStatus()));
        if (hasRejected) {
            return "REJECTED";
        }

        boolean allVerified = documents.stream().allMatch(doc -> "VERIFIED".equals(doc.getVerificationStatus()));
        if (allVerified) {
            return "VERIFIED";
        }

        boolean hasInProgress = documents.stream().anyMatch(doc -> "IN_PROGRESS".equals(doc.getVerificationStatus()));
        if (hasInProgress) {
            return "IN_PROGRESS";
        }

        return "PENDING";
    }
}