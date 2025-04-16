//package com.synapsecode.accountservice.dto.request;
//
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.DecimalMin;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class AccountUpdateRequest {
//    private String accountHolderName;
//
//    @Email(message = "Email must be valid")
//    private String email;
//
//    private String phoneNumber;
//    private String alternatePhoneNumber;
//    private List<AccountCreationRequest.AddressRequest> addresses;
//    private String preferredContactMethod;
//
//    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum balance cannot be negative")
//    private BigDecimal minimumBalance;
//
//    @DecimalMin(value = "0.0", inclusive = true, message = "Overdraft limit cannot be negative")
//    private BigDecimal overdraftLimit;
//
//    private Boolean isOverdraftEnabled;
//    private AccountCreationRequest.AccountPreferenceRequest preferences;
//}
package com.synapsecode.accountservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.util.List;

public record AccountUpdateRequest(
        String accountHolderName,

        @Email(message = "Email must be valid")
        String email,

        String phoneNumber,
        String alternatePhoneNumber,
        List<AccountCreationRequest.AddressRequest> addresses,
        String preferredContactMethod,

        @DecimalMin(value = "0.0", inclusive = true, message = "Minimum balance cannot be negative")
        BigDecimal minimumBalance,

        @DecimalMin(value = "0.0", inclusive = true, message = "Overdraft limit cannot be negative")
        BigDecimal overdraftLimit,

        Boolean isOverdraftEnabled,
        AccountCreationRequest.AccountPreferenceRequest preferences
) {}