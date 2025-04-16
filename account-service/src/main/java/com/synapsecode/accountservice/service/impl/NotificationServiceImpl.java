//package com.synapsecode.accountservice.service.impl;
//
//import com.synapsecode.accountservice.entity.Account;
//import com.synapsecode.accountservice.entity.AccountStatus;
//import com.synapsecode.accountservice.entity.KycDocument;
//import com.synapsecode.accountservice.service.NotificationService;
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
//public class NotificationServiceImpl implements NotificationService {
//
//    @Value("${app.notifications.enabled:true}")
//    private boolean notificationsEnabled;
//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
////    public NotificationService(KafkaTemplate<String, Object> kafkaTemplate) {
////        this.kafkaTemplate = kafkaTemplate;
////    }
//
//    @Override
//    public void sendAccountCreationNotification(Account account) {
//        if (!notificationsEnabled) return;
//
//        Map<String, Object> notificationData = new HashMap<>();
//        notificationData.put("type", "ACCOUNT_CREATION");
//        notificationData.put("accountId", account.getId().toString());
//        notificationData.put("accountNumber", account.getAccountNumber());
//        notificationData.put("customerId", account.getCustomerId().toString());
//        notificationData.put("timestamp", LocalDateTime.now().toString());
//        notificationData.put("email", account.getContactDetails().getEmail());
//        notificationData.put("phone", account.getContactDetails().getPhoneNumber());
//        notificationData.put("templateId", "account_creation");
//
//        log.info("Sending account creation notification for account: {}", account.getAccountNumber());
//        kafkaTemplate.send("notification-events", notificationData);
//    }
//
//    @Override
//    public void sendAccountStatusChangeNotification(Account account, AccountStatus previousStatus) {
//        if (!notificationsEnabled) return;
//
//        Map<String, Object> notificationData = new HashMap<>();
//        notificationData.put("type", "ACCOUNT_STATUS_CHANGE");
//        notificationData.put("accountId", account.getId().toString());
//        notificationData.put("accountNumber", account.getAccountNumber());
//        notificationData.put("customerId", account.getCustomerId().toString());
//        notificationData.put("timestamp", LocalDateTime.now().toString());
//        notificationData.put("email", account.getContactDetails().getEmail());
//        notificationData.put("phone", account.getContactDetails().getPhoneNumber());
//        notificationData.put("previousStatus", previousStatus.toString());
//        notificationData.put("currentStatus", account.getStatus().toString());
//        notificationData.put("templateId", "account_status_change");
//
//        log.info("Sending account status change notification for account: {}", account.getAccountNumber());
//        kafkaTemplate.send("notification-events", notificationData);
//    }
//
//    @Override
//    public void sendAccountClosureNotification(Account account) {
//        if (!notificationsEnabled) return;
//
//        Map<String, Object> notificationData = new HashMap<>();
//        notificationData.put("type", "ACCOUNT_CLOSURE");
//        notificationData.put("accountId", account.getId().toString());
//        notificationData.put("accountNumber", account.getAccountNumber());
//        notificationData.put("customerId", account.getCustomerId().toString());
//        notificationData.put("timestamp", LocalDateTime.now().toString());
//        notificationData.put("email", account.getContactDetails().getEmail());
//        notificationData.put("phone", account.getContactDetails().getPhoneNumber());
//        notificationData.put("reason", account.getClosureReason() != null ? account.getClosureReason() : "Not specified");
//        notificationData.put("templateId", "account_closure");
//
//        log.info("Sending account closure notification for account: {}", account.getAccountNumber());
//        kafkaTemplate.send("notification-events", notificationData);
//    }
//
//    @Override
//    public void sendBalanceAlertNotification(Account account, BigDecimal previousBalance, BigDecimal newBalance) {
//        if (!notificationsEnabled) return;
//        if (!account.getPreferences().getTransactionAlerts()) return;
//
//        BigDecimal threshold = BigDecimal.valueOf(account.getPreferences().getThresholdAlertAmount());
//        boolean belowThreshold = newBalance.compareTo(threshold) < 0 && previousBalance.compareTo(threshold) >= 0;
//
//        if (belowThreshold) {
//            Map<String, Object> notificationData = new HashMap<>();
//            notificationData.put("type", "BALANCE_ALERT");
//            notificationData.put("accountId", account.getId().toString());
//            notificationData.put("accountNumber", account.getAccountNumber());
//            notificationData.put("customerId", account.getCustomerId().toString());
//            notificationData.put("timestamp", LocalDateTime.now().toString());
//            notificationData.put("email", account.getContactDetails().getEmail());
//            notificationData.put("phone", account.getContactDetails().getPhoneNumber());
//            notificationData.put("balance", newBalance);
//            notificationData.put("threshold", threshold);
//            notificationData.put("templateId", "balance_alert");
//
//            log.info("Sending balance alert notification for account: {}", account.getAccountNumber());
//            kafkaTemplate.send("notification-events", notificationData);
//        }
//    }
//
//    @Override
//    public void sendKycUpdateNotification(Account account) {
//        if (!notificationsEnabled) return;
//
//        Map<String, Object> notificationData = new HashMap<>();
//        notificationData.put("type", "KYC_UPDATE");
//        notificationData.put("accountId", account.getId().toString());
//        notificationData.put("accountNumber", account.getAccountNumber());
//        notificationData.put("customerId", account.getCustomerId().toString());
//        notificationData.put("timestamp", LocalDateTime.now().toString());
//        notificationData.put("email", account.getContactDetails().getEmail());
//        notificationData.put("phone", account.getContactDetails().getPhoneNumber());
////        notificationData.put("kycStatus", account.getKycDocuments().getVerificationStatus);
//        Map<String, String> documentStatus = new HashMap<>();
//        for(KycDocument kycDocument : account.getKycDocuments()){
//            documentStatus.put(kycDocument.getDocumentNumber(), kycDocument.getVerificationStatus());
//        }
//        notificationData.put("kycStatus", documentStatus);
//        notificationData.put("templateId", "kyc_update");
//
//        log.info("Sending KYC update notification for account: {}", account.getAccountNumber());
//        kafkaTemplate.send("notification-events", notificationData);
//    }
//}

package com.synapsecode.accountservice.service.impl;

import com.synapsecode.accountservice.entity.Account;
import com.synapsecode.accountservice.entity.AccountStatus;
import com.synapsecode.accountservice.entity.KycDocument;
import com.synapsecode.accountservice.service.NotificationService;
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
public class NotificationServiceImpl implements NotificationService {

    @Value("${app.notifications.enabled:true}")
    private boolean notificationsEnabled;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendAccountCreationNotification(Account account) {
        if (!notificationsEnabled) return;

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("type", "ACCOUNT_CREATION");
        notificationData.put("accountId", account.getId().toString());
        notificationData.put("accountNumber", account.getAccountNumber());
        notificationData.put("customerId", account.getCustomerId().toString());
        notificationData.put("timestamp", LocalDateTime.now().toString());
        notificationData.put("email", account.getContactDetails().getEmail());
        notificationData.put("phone", account.getContactDetails().getPhoneNumber());
        notificationData.put("templateId", "account_creation");

        log.info("Sending account creation notification for account: {}", account.getAccountNumber());
        rabbitTemplate.convertAndSend("notification-exchange", "notification.account.creation", notificationData);
    }

    @Override
    public void sendAccountStatusChangeNotification(Account account, AccountStatus previousStatus) {
        if (!notificationsEnabled) return;

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("type", "ACCOUNT_STATUS_CHANGE");
        notificationData.put("accountId", account.getId().toString());
        notificationData.put("accountNumber", account.getAccountNumber());
        notificationData.put("customerId", account.getCustomerId().toString());
        notificationData.put("timestamp", LocalDateTime.now().toString());
        notificationData.put("email", account.getContactDetails().getEmail());
        notificationData.put("phone", account.getContactDetails().getPhoneNumber());
        notificationData.put("previousStatus", previousStatus.toString());
        notificationData.put("currentStatus", account.getStatus().toString());
        notificationData.put("templateId", "account_status_change");

        log.info("Sending account status change notification for account: {}", account.getAccountNumber());
        rabbitTemplate.convertAndSend("notification-exchange", "notification.account.status", notificationData);
    }

    @Override
    public void sendAccountClosureNotification(Account account) {
        if (!notificationsEnabled) return;

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("type", "ACCOUNT_CLOSURE");
        notificationData.put("accountId", account.getId().toString());
        notificationData.put("accountNumber", account.getAccountNumber());
        notificationData.put("customerId", account.getCustomerId().toString());
        notificationData.put("timestamp", LocalDateTime.now().toString());
        notificationData.put("email", account.getContactDetails().getEmail());
        notificationData.put("phone", account.getContactDetails().getPhoneNumber());
        notificationData.put("reason", account.getClosureReason() != null ? account.getClosureReason() : "Not specified");
        notificationData.put("templateId", "account_closure");

        log.info("Sending account closure notification for account: {}", account.getAccountNumber());
        rabbitTemplate.convertAndSend("notification-exchange", "notification.account.closure", notificationData);
    }

    @Override
    public void sendBalanceAlertNotification(Account account, BigDecimal previousBalance, BigDecimal newBalance) {
        if (!notificationsEnabled) return;
        if (!account.getPreferences().getTransactionAlerts()) return;

        BigDecimal threshold = BigDecimal.valueOf(account.getPreferences().getThresholdAlertAmount());
        boolean belowThreshold = newBalance.compareTo(threshold) < 0 && previousBalance.compareTo(threshold) >= 0;

        if (belowThreshold) {
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("type", "BALANCE_ALERT");
            notificationData.put("accountId", account.getId().toString());
            notificationData.put("accountNumber", account.getAccountNumber());
            notificationData.put("customerId", account.getCustomerId().toString());
            notificationData.put("timestamp", LocalDateTime.now().toString());
            notificationData.put("email", account.getContactDetails().getEmail());
            notificationData.put("phone", account.getContactDetails().getPhoneNumber());
            notificationData.put("balance", newBalance);
            notificationData.put("threshold", threshold);
            notificationData.put("templateId", "balance_alert");

            log.info("Sending balance alert notification for account: {}", account.getAccountNumber());
            rabbitTemplate.convertAndSend("notification-exchange", "notification.account.balance", notificationData);
        }
    }

    @Override
    public void sendKycUpdateNotification(Account account) {
        if (!notificationsEnabled) return;

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("type", "KYC_UPDATE");
        notificationData.put("accountId", account.getId().toString());
        notificationData.put("accountNumber", account.getAccountNumber());
        notificationData.put("customerId", account.getCustomerId().toString());
        notificationData.put("timestamp", LocalDateTime.now().toString());
        notificationData.put("email", account.getContactDetails().getEmail());
        notificationData.put("phone", account.getContactDetails().getPhoneNumber());

        Map<String, String> documentStatus = new HashMap<>();
        for(KycDocument kycDocument : account.getKycDocuments()){
            documentStatus.put(kycDocument.getDocumentNumber(), kycDocument.getVerificationStatus());
        }
        notificationData.put("kycStatus", documentStatus);
        notificationData.put("templateId", "kyc_update");

        log.info("Sending KYC update notification for account: {}", account.getAccountNumber());
        rabbitTemplate.convertAndSend("notification-exchange", "notification.kyc.update", notificationData);
    }
}