//package com.synapsecode.accountservice.dto.request;
//
//import com.synapsecode.accountservice.entity.DocumentType;
//import jakarta.validation.constraints.*;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Getter
//@Setter
//public class AccountCreationRequest {
//    @NotBlank(message = "Account holder name is required")
//    private String accountHolderName;
//
//    @NotBlank(message = "Account type is required")
//    private String accountType;
//
//    @NotBlank(message = "Branch code is required")
//    private String branchCode;
//
//    @NotBlank(message = "Currency is required")
//    private String currency;
//
//    @NotNull(message = "Initial deposit amount is required")
//    @DecimalMin(value = "0.0", inclusive = true, message = "Initial deposit cannot be negative")
//    private BigDecimal initialDeposit;
//
//    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum balance cannot be negative")
//    private BigDecimal minimumBalance;
//
//    @DecimalMin(value = "0.0", inclusive = true, message = "Overdraft limit cannot be negative")
//    private BigDecimal overdraftLimit;
//
//    private Boolean isOverdraftEnabled;
//
//    @NotBlank(message = "Email is required")
//    @Email(message = "Email must be valid")
//    private String email;
//
//    @NotBlank(message = "Phone number is required")
//    private String phoneNumber;
//
//    private String alternatePhoneNumber;
//
//    @NotEmpty(message = "At least one address is required")
//    private List<AddressRequest> addresses;
//
//    @NotNull(message = "Date of birth is required")
//    @Past(message = "Date of birth must be in the past")
//    private LocalDate dateOfBirth;
//
//    @NotBlank(message = "Nationality is required")
//    private String nationality;
//
//    private String taxIdNumber;
//
//    @NotNull(message = "Customer ID is required")
//    private UUID customerId;
//
//    @NotEmpty(message = "At least one KYC document is required")
//    private List<KycDocumentRequest> kycDocuments;
//
//    private AccountPreferenceRequest preferences;
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class AddressRequest {
//        @NotBlank(message = "Address type is required")
//        private String addressType;
//
//        @NotBlank(message = "Address line 1 is required")
//        private String addressLine1;
//
//        private String addressLine2;
//
//        @NotBlank(message = "City is required")
//        private String city;
//
//        @NotBlank(message = "State is required")
//        private String state;
//
//        @NotBlank(message = "Postal code is required")
//        private String postalCode;
//
//        @NotBlank(message = "Country is required")
//        private String country;
//
//        private Boolean isPrimary;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class KycDocumentRequest {
//        @NotBlank(message = "Document type is required")
//        private DocumentType documentType;
//
//        @NotBlank(message = "Document number is required")
//        private String documentNumber;
//
//        @NotBlank(message = "Issuing authority is required")
//        private String issuingAuthority;
//
//        @NotNull(message = "Issue date is required")
//        @Past(message = "Issue date must be in the past")
//        private LocalDate issueDate;
//
//        @NotNull(message = "Expiry date is required")
//        @Future(message = "Expiry date must be in the future")
//        private LocalDate expiryDate;
//
//        @NotBlank(message = "Document path is required")
//        private String documentPath;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class AccountPreferenceRequest {
//        private String statementFrequency;
//        private String statementDeliveryMode;
//        private Boolean notificationPreference;
//        private Boolean transactionAlerts;
//        private Boolean balanceAlerts;
//        private Boolean marketingCommunications;
//        private Double thresholdAlertAmount;
//        private String preferredLanguage;
//    }
//}

package com.synapsecode.accountservice.dto.request;

import com.synapsecode.accountservice.entity.DocumentType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AccountCreationRequest(
        @NotBlank(message = "Account holder name is required")
        String accountHolderName,

        @NotBlank(message = "Account type is required")
        String accountType,

        @NotBlank(message = "Branch code is required")
        String branchCode,

        @NotBlank(message = "Currency is required")
        String currency,

        @NotNull(message = "Initial deposit amount is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Initial deposit cannot be negative")
        BigDecimal initialDeposit,

        @DecimalMin(value = "0.0", inclusive = true, message = "Minimum balance cannot be negative")
        BigDecimal minimumBalance,

        @DecimalMin(value = "0.0", inclusive = true, message = "Overdraft limit cannot be negative")
        BigDecimal overdraftLimit,

        Boolean isOverdraftEnabled,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        String alternatePhoneNumber,

        @NotEmpty(message = "At least one address is required")
        List<AddressRequest> addresses,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotBlank(message = "Nationality is required")
        String nationality,

        String taxIdNumber,

        @NotNull(message = "Customer ID is required")
        UUID customerId,

        @NotEmpty(message = "At least one KYC document is required")
        List<KycDocumentRequest> kycDocuments,

        AccountPreferenceRequest preferences
) {
    // Nested records for the inner classes
    public record AddressRequest(
            @NotBlank(message = "Address type is required")
            String addressType,

            @NotBlank(message = "Address line 1 is required")
            String addressLine1,

            String addressLine2,

            @NotBlank(message = "City is required")
            String city,

            @NotBlank(message = "State is required")
            String state,

            @NotBlank(message = "Postal code is required")
            String postalCode,

            @NotBlank(message = "Country is required")
            String country,

            Boolean isPrimary
    ) {}

    public record KycDocumentRequest(
            @NotBlank(message = "Document type is required")
            DocumentType documentType,

            @NotBlank(message = "Document number is required")
            String documentNumber,

            @NotBlank(message = "Issuing authority is required")
            String issuingAuthority,

            @NotNull(message = "Issue date is required")
            @Past(message = "Issue date must be in the past")
            LocalDate issueDate,

            @NotNull(message = "Expiry date is required")
            @Future(message = "Expiry date must be in the future")
            LocalDate expiryDate,

            @NotBlank(message = "Document path is required")
            String documentPath
    ) {}

    public record AccountPreferenceRequest(
            String statementFrequency,
            String statementDeliveryMode,
            Boolean notificationPreference,
            Boolean transactionAlerts,
            Boolean balanceAlerts,
            Boolean marketingCommunications,
            Double thresholdAlertAmount,
            String preferredLanguage
    ) {}
}