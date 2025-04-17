package com.synapsecode.accountservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
public record KycValidationEvent(
         UUID accountId,
         Long documentId,
         String eventType,  // START, COMPLETE, FAILED
         String status,
         String message
) {
//    private UUID accountId;
//    private Long documentId;
//    private String eventType;  // START, COMPLETE, FAILED
//    private String status;
//    private String message;
}