package com.synapsecode.accountservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_preferences")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AccountPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "statement_frequency")
    private String statementFrequency; // MONTHLY, QUARTERLY, etc.

    @Column(name = "statement_delivery_mode")
    private String statementDeliveryMode; // EMAIL, MAIL, BOTH

    @Column(name = "notification_preference", nullable = false)
    private Boolean notificationPreference;

    @Column(name = "transaction_alerts", nullable = false)
    private Boolean transactionAlerts;

    @Column(name = "balance_alerts", nullable = false)
    private Boolean balanceAlerts;

    @Column(name = "marketing_communications", nullable = false)
    private Boolean marketingCommunications;

    @Column(name = "threshold_alert_amount")
    private Double thresholdAlertAmount;

    @Column(name = "preferred_language")
    private String preferredLanguage;

    @OneToOne(mappedBy = "preferences")
    private Account account;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
}