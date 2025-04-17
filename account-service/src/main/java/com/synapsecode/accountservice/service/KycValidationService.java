package com.synapsecode.accountservice.service;

import com.synapsecode.accountservice.entity.KycDocument;

import java.util.UUID;

public interface KycValidationService {
    void validateKycDocument(KycDocument kycDocument);

    void validateDocumentExpiry(KycDocument document);

    void validateEmail(String email);

    void validatePhoneNumber(String phoneNumber);

    void validateAddress(String addressLine1, String city, String state, String country, String postalCode);

    void startValidation(UUID accountId);
}
