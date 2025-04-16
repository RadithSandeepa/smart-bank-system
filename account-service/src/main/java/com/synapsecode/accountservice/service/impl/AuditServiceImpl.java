//package com.synapsecode.accountservice.service.impl;
//
//import com.synapsecode.accountservice.entity.Account;
//import com.synapsecode.accountservice.entity.AccountStatus;
//import com.synapsecode.accountservice.entity.KycDocument;
//import com.synapsecode.accountservice.service.AuditService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class AuditServiceImpl implements AuditService {
//
//    @Value("${app.audit.enabled:true}")
//    private boolean auditEnabled;
//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
////    public AuditService(KafkaTemplate<String, Object> kafkaTemplate) {
////        this.kafkaTemplate = kafkaTemplate;
////    }
//
//    @Override
//    public void recordAccountCreation(Account account) {
//        if (!auditEnabled) return;
//
//        Map<String, Object> auditData = new HashMap<>();
//        auditData.put("eventType", "ACCOUNT_CREATION");
//        auditData.put("accountId", account.getId().toString());
//        auditData.put("accountNumber", account.getAccountNumber());
//        auditData.put("customerId", account.getCustomerId().toString());
//        auditData.put("timestamp", LocalDateTime.now().toString());
//        auditData.put("status", account.getStatus().toString());
//
//        log.info("Audit: Account creation - AccountID: {}, AccountNumber: {}",
//                account.getId(), account.getAccountNumber());
//        kafkaTemplate.send("account-audit-events", auditData);
//    }
//
//    @Override
//    public void recordAccountUpdate(Account account) {
//        if (!auditEnabled) return;
//
//        Map<String, Object> auditData = new HashMap<>();
//        auditData.put("eventType", "ACCOUNT_UPDATE");
//        auditData.put("accountId", account.getId().toString());
//        auditData.put("accountNumber", account.getAccountNumber());
//        auditData.put("timestamp", LocalDateTime.now().toString());
//        auditData.put("status", account.getStatus().toString());
//
//        log.info("Audit: Account update - AccountID: {}, AccountNumber: {}",
//                account.getId(), account.getAccountNumber());
//        kafkaTemplate.send("account-audit-events", auditData);
//    }
//
//    @Override
//    public void recordAccountStatusChange(Account account, AccountStatus previousStatus) {
//        if (!auditEnabled) return;
//
//        Map<String, Object> auditData = new HashMap<>();
//        auditData.put("eventType", "ACCOUNT_STATUS_CHANGE");
//        auditData.put("accountId", account.getId().toString());
//        auditData.put("accountNumber", account.getAccountNumber());
//        auditData.put("previousStatus", previousStatus.toString());
//        auditData.put("currentStatus", account.getStatus().toString());
//        auditData.put("timestamp", LocalDateTime.now().toString());
//
//        log.info("Audit: Account status change - AccountID: {}, From: {} To: {}",
//                account.getId(), previousStatus, account.getStatus());
//        kafkaTemplate.send("account-audit-events", auditData);
//    }
//
//    @Override
//    public void recordAccountClosure(Account account) {
//        if (!auditEnabled) return;
//
//        Map<String, Object> auditData = new HashMap<>();
//        auditData.put("eventType", "ACCOUNT_CLOSURE");
//        auditData.put("accountId", account.getId().toString());
//        auditData.put("accountNumber", account.getAccountNumber());
//        auditData.put("timestamp", LocalDateTime.now().toString());
//        auditData.put("reason", account.getClosureReason());
//
//        log.info("Audit: Account closure - AccountID: {}, AccountNumber: {}, Reason: {}",
//                account.getId(), account.getAccountNumber(), account.getClosureReason());
//        kafkaTemplate.send("account-audit-events", auditData);
//    }
//
//    @Override
//    public void recordBalanceChange(Account account, BigDecimal previousBalance, BigDecimal newBalance) {
//        if (!auditEnabled) return;
//
//        Map<String, Object> auditData = new HashMap<>();
//        auditData.put("eventType", "BALANCE_CHANGE");
//        auditData.put("accountId", account.getId().toString());
//        auditData.put("accountNumber", account.getAccountNumber());
//        auditData.put("previousBalance", previousBalance);
//        auditData.put("newBalance", newBalance);
//        auditData.put("difference", newBalance.subtract( previousBalance));
//        auditData.put("timestamp", LocalDateTime.now().toString());
//
//        log.info("Audit: Balance change - AccountID: {}, From: {} To: {}",
//                account.getId(), previousBalance, newBalance);
//        kafkaTemplate.send("account-audit-events", auditData);
//    }
//
//    @Override
//    public void recordKycUpdate(Account account) {
//        if (!auditEnabled) return;
//
//        Map<String, Object> auditData = new HashMap<>();
//        auditData.put("eventType", "KYC_UPDATE");
//        auditData.put("accountId", account.getId().toString());
//        auditData.put("accountNumber", account.getAccountNumber());
//        auditData.put("timestamp", LocalDateTime.now().toString());
////        auditData.put("kycStatus", account.getKycDocuments().getVerificationStatus);
//
//        Map<String, String> documentStatus = new HashMap<>();
//        for(KycDocument kycDocument : account.getKycDocuments()){
//            documentStatus.put(kycDocument.getDocumentNumber(), kycDocument.getVerificationStatus());
//        }
//        auditData.put("kycStatus", documentStatus);
//
//        log.info("Audit: KYC update - AccountID: {}, Status: {}",
//                account.getId(), documentStatus);
//        kafkaTemplate.send("account-audit-events", auditData);
//    }
//}
package com.synapsecode.accountservice.service.impl;

import com.synapsecode.accountservice.entity.Account;
import com.synapsecode.accountservice.entity.AccountStatus;
import com.synapsecode.accountservice.entity.KycDocument;
import com.synapsecode.accountservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    @Value("${app.audit.enabled:true}")
    private boolean auditEnabled;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void recordAccountCreation(Account account) {
        if (!auditEnabled) return;

        Map<String, Object> auditData = new HashMap<>();
        auditData.put("eventType", "ACCOUNT_CREATION");
        auditData.put("accountId", account.getId().toString());
        auditData.put("accountNumber", account.getAccountNumber());
        auditData.put("customerId", account.getCustomerId().toString());
        auditData.put("timestamp", LocalDateTime.now().toString());
        auditData.put("status", account.getStatus().toString());

        log.info("Audit: Account creation - AccountID: {}, AccountNumber: {}",
                account.getId(), account.getAccountNumber());
        rabbitTemplate.convertAndSend("account-audit-exchange", "account.audit.creation", auditData);
    }

    @Override
    public void recordAccountUpdate(Account account) {
        if (!auditEnabled) return;

        Map<String, Object> auditData = new HashMap<>();
        auditData.put("eventType", "ACCOUNT_UPDATE");
        auditData.put("accountId", account.getId().toString());
        auditData.put("accountNumber", account.getAccountNumber());
        auditData.put("timestamp", LocalDateTime.now().toString());
        auditData.put("status", account.getStatus().toString());

        log.info("Audit: Account update - AccountID: {}, AccountNumber: {}",
                account.getId(), account.getAccountNumber());
        rabbitTemplate.convertAndSend("account-audit-exchange", "account.audit.update", auditData);
    }

    @Override
    public void recordAccountStatusChange(Account account, AccountStatus previousStatus) {
        if (!auditEnabled) return;

        Map<String, Object> auditData = new HashMap<>();
        auditData.put("eventType", "ACCOUNT_STATUS_CHANGE");
        auditData.put("accountId", account.getId().toString());
        auditData.put("accountNumber", account.getAccountNumber());
        auditData.put("previousStatus", previousStatus.toString());
        auditData.put("currentStatus", account.getStatus().toString());
        auditData.put("timestamp", LocalDateTime.now().toString());

        log.info("Audit: Account status change - AccountID: {}, From: {} To: {}",
                account.getId(), previousStatus, account.getStatus());
        rabbitTemplate.convertAndSend("account-audit-exchange", "account.audit.status", auditData);
    }

    @Override
    public void recordAccountClosure(Account account) {
        if (!auditEnabled) return;

        Map<String, Object> auditData = new HashMap<>();
        auditData.put("eventType", "ACCOUNT_CLOSURE");
        auditData.put("accountId", account.getId().toString());
        auditData.put("accountNumber", account.getAccountNumber());
        auditData.put("timestamp", LocalDateTime.now().toString());
        auditData.put("reason", account.getClosureReason());

        log.info("Audit: Account closure - AccountID: {}, AccountNumber: {}, Reason: {}",
                account.getId(), account.getAccountNumber(), account.getClosureReason());
        rabbitTemplate.convertAndSend("account-audit-exchange", "account.audit.closure", auditData);
    }

    @Override
    public void recordBalanceChange(Account account, BigDecimal previousBalance, BigDecimal newBalance) {
        if (!auditEnabled) return;

        Map<String, Object> auditData = new HashMap<>();
        auditData.put("eventType", "BALANCE_CHANGE");
        auditData.put("accountId", account.getId().toString());
        auditData.put("accountNumber", account.getAccountNumber());
        auditData.put("previousBalance", previousBalance);
        auditData.put("newBalance", newBalance);
        auditData.put("difference", newBalance.subtract(previousBalance));
        auditData.put("timestamp", LocalDateTime.now().toString());

        log.info("Audit: Balance change - AccountID: {}, From: {} To: {}",
                account.getId(), previousBalance, newBalance);
        rabbitTemplate.convertAndSend("account-audit-exchange", "account.audit.balance", auditData);
    }

    @Override
    public void recordKycUpdate(Account account) {
        if (!auditEnabled) return;

        Map<String, Object> auditData = new HashMap<>();
        auditData.put("eventType", "KYC_UPDATE");
        auditData.put("accountId", account.getId().toString());
        auditData.put("accountNumber", account.getAccountNumber());
        auditData.put("timestamp", LocalDateTime.now().toString());

        Map<String, String> documentStatus = new HashMap<>();
        for(KycDocument kycDocument : account.getKycDocuments()){
            documentStatus.put(kycDocument.getDocumentNumber(), kycDocument.getVerificationStatus());
        }
        auditData.put("kycStatus", documentStatus);

        log.info("Audit: KYC update - AccountID: {}, Status: {}",
                account.getId(), documentStatus);
        rabbitTemplate.convertAndSend("account-audit-exchange", "account.audit.kyc", auditData);
    }
}