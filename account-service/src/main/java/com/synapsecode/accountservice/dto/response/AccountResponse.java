//package com.synapsecode.accountservice.dto.response;
//
//import com.synapsecode.accountservice.entity.AccountStatus;
//import com.synapsecode.accountservice.entity.DocumentType;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class AccountResponse {
//    private UUID id;
//    private String accountNumber;
//    private String accountHolderName;
//    private AccountStatus status;
//    private String accountType;
//    private String branchCode;
//    private BigDecimal currentBalance;
//    private BigDecimal availableBalance;
//    private BigDecimal minimumBalance;
//    private BigDecimal overdraftLimit;
//    private Boolean isOverdraftEnabled;
//    private String currency;
//    private LocalDateTime openingDate;
//    private LocalDateTime lastActiveDate;
//    private ContactDetailsResponse contactDetails;
//    private List<AddressResponse> addresses;
//    private List<KycDocumentResponse> kycDocuments;
//    private AccountPreferenceResponse preferences;
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class ContactDetailsResponse {
//        private String email;
//        private String phoneNumber;
//        private String alternatePhoneNumber;
//        private Boolean isEmailVerified;
//        private Boolean isPhoneVerified;
//        private String preferredContactMethod;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class AddressResponse {
//        private UUID id;
//        private String addressType;
//        private String addressLine1;
//        private String addressLine2;
//        private String city;
//        private String state;
//        private String postalCode;
//        private String country;
//        private Boolean isPrimary;
//        private Boolean isVerified;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class KycDocumentResponse {
//        private UUID id;
//        private DocumentType documentType;
//        private String documentNumber;
//        private String issuingAuthority;
//        private LocalDate issueDate;
//        private LocalDate expiryDate;
//        private String verificationStatus;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class AccountPreferenceResponse {
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

package com.synapsecode.accountservice.dto.response;

import com.synapsecode.accountservice.entity.AccountStatus;
import com.synapsecode.accountservice.entity.DocumentType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record AccountResponse(
        UUID id,
        String accountNumber,
        String accountHolderName,
        AccountStatus status,
        String accountType,
        String branchCode,
        BigDecimal currentBalance,
        BigDecimal availableBalance,
        BigDecimal minimumBalance,
        BigDecimal overdraftLimit,
        Boolean isOverdraftEnabled,
        String currency,
        LocalDateTime openingDate,
        LocalDateTime lastActiveDate,
        ContactDetailsResponse contactDetails,
        List<AddressResponse> addresses,
        List<KycDocumentResponse> kycDocuments,
        AccountPreferenceResponse preferences
) {
    // Nested records for the inner classes
    @Builder
    public record ContactDetailsResponse(
            String email,
            String phoneNumber,
            String alternatePhoneNumber,
            Boolean isEmailVerified,
            Boolean isPhoneVerified,
            String preferredContactMethod
    ) {}

    @Builder
    public record AddressResponse(
            UUID id,
            String addressType,
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country,
            Boolean isPrimary,
            Boolean isVerified
    ) {}

    @Builder
    public record KycDocumentResponse(
            UUID id,
            DocumentType documentType,
            String documentNumber,
            String issuingAuthority,
            LocalDate issueDate,
            LocalDate expiryDate,
            String verificationStatus
    ) {}

    @Builder
    public record AccountPreferenceResponse(
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