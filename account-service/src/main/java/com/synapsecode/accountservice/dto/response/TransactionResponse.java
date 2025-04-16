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
//public class TransactionResponse {
//    private UUID transactionId;
//    private String transactionType;
//    private BigDecimal amount;
//    private String description;
//    private LocalDateTime transactionDate;
//    private String reference;
//    private String status;
//    private BigDecimal balanceAfterTransaction;
//    private String currency;
//}

package com.synapsecode.accountservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID transactionId,
        String transactionType,
        BigDecimal amount,
        String description,
        LocalDateTime transactionDate,
        String reference,
        String status,
        BigDecimal balanceAfterTransaction,
        String currency
) {}