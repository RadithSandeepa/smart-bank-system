package com.synapsecode.accountservice.listener;

import com.synapsecode.accountservice.entity.AccountStatus;
import com.synapsecode.accountservice.event.KycValidationEvent;
import com.synapsecode.accountservice.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class KycValidationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(KycValidationEventListener.class);

    @Autowired
    private AccountRepository accountRepository;

    @EventListener
    public void handleKycValidationEvent(KycValidationEvent event) {
        logger.info("Received KYC validation event: {} for account: {}", event.eventType(), event.accountId());

        // Update account KYC status based on validation result
        if ("COMPLETE".equals(event.eventType())) {
            accountRepository.findById(event.accountId()).ifPresent(account -> {
                // Update account KYC status
                account.getKycDocuments().stream()
                        .filter(kycDocument -> kycDocument.getId().equals(event.documentId()))
                        .findFirst()
                        .ifPresent(kycDocument -> {
                            kycDocument.setVerificationStatus(event.status());
                            kycDocument.setVerificationComments(event.message());
                            kycDocument.setVerificationDate(LocalDateTime.now());
                        });

//                account.setKycStatus(event.getStatus());

                // If KYC is complete and account is in PENDING status, activate it
                if ("COMPLETE".equals(event.status()) &&
                        "PENDING".equals(account.getStatus().name())) {
                    account.setStatus(AccountStatus.ACTIVE);
                }

                accountRepository.save(account);
                logger.info("Updated account KYC status to: {} for account: {}",
                        event.status(), event.accountId());
            });
        }
    }
}