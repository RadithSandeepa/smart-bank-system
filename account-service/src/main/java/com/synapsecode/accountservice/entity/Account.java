package com.synapsecode.accountservice.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountHolderName;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    private String branchCode;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "available_balance", nullable = false)
    private BigDecimal availableBalance;

    @Column(name = "minimum_balance", nullable = false)
    private BigDecimal minimumBalance;

    @Column(name = "overdraft_limit")
    private BigDecimal overdraftLimit;

    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String currency;

    @Column(name = "tax_id_number")
    private String taxIdNumber;

    @Column(name = "is_overdraft_enabled")
    private Boolean isOverdraftEnabled;

    @Column(name = "opening_date", nullable = false)
    private LocalDateTime openingDate;

    @Column(name = "last_active_date")
    private LocalDateTime lastActiveDate;

    private String nationality;

    @Column(name = "customerID", nullable = false)
    private UUID customerId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_details_id")
    private ContactDetails contactDetails;

    @ElementCollection
    @CollectionTable(name = "transaction_references", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "transaction_reference")
    private List<String> transactionReference;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<KycDocument> kycDocuments = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "preference_id")
    private AccountPreference preferences;

    @Column(name = "closed_date")
    private LocalDateTime closedDate;

    @Column(name = "closure_reason")
    private String closureReason;

    @Column(name = "risk_rating")
    private String riskRating;

    // Audit fields
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Version
    private Integer version;

    @PrePersist
    protected void onCreate() {
        if (openingDate == null) {
            openingDate = LocalDateTime.now();
        }
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }
        if (availableBalance == null) {
            availableBalance = BigDecimal.ZERO;
        }
        if (status == null) {
            status = AccountStatus.PENDING;
        }
    }
}