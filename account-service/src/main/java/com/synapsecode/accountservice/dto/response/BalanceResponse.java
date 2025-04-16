//package com.synapsecode.accountservice.dto.response;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class BalanceResponse {
//    private UUID accountId;
//    private String accountNumber;
//    private BigDecimal currentBalance;
//    private BigDecimal availableBalance;
//    private BigDecimal minimumBalance;
//    private BigDecimal overdraftLimit;
//    private BigDecimal availableOverdraft;
//    private String currency;
//    private LocalDateTime lastUpdated;
//}
package com.synapsecode.accountservice.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record BalanceResponse(
        UUID accountId,
        String accountNumber,
        BigDecimal currentBalance,
        BigDecimal availableBalance,
        BigDecimal minimumBalance,
        BigDecimal overdraftLimit,
        BigDecimal availableOverdraft,
        String currency,
        LocalDateTime lastUpdated
) {}