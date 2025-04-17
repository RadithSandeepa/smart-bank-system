package com.synapsecode.accountservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contact_details")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ContactDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "alternate_phone_number")
    private String alternatePhoneNumber;

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified;

    @Column(name = "is_phone_verified")
    private Boolean isPhoneVerified;

    @Column(name = "email_verification_date")
    private LocalDateTime emailVerificationDate;

    @Column(name = "phone_verification_date")
    private LocalDateTime phoneVerificationDate;

    @Column(name = "preferred_contact_method")
    private String preferredContactMethod;

    @OneToOne(mappedBy = "contactDetails")
    private Account account;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
}