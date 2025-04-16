package com.synapsecode.accountservice.service.impl;


import com.synapsecode.accountservice.dto.request.AccountCreationRequest;
import com.synapsecode.accountservice.dto.request.AccountUpdateRequest;
import com.synapsecode.accountservice.dto.response.AccountResponse;
import com.synapsecode.accountservice.dto.response.BalanceResponse;
import com.synapsecode.accountservice.dto.response.TransactionResponse;
import com.synapsecode.accountservice.entity.*;
import com.synapsecode.accountservice.exception.AccountNotFoundException;
import com.synapsecode.accountservice.exception.InsufficientDataException;
import com.synapsecode.accountservice.exception.KycValidationException;
import com.synapsecode.accountservice.repository.AccountRepository;
import com.synapsecode.accountservice.repository.AddressRepository;
import com.synapsecode.accountservice.repository.ContactDetailsRepository;
import com.synapsecode.accountservice.repository.KycDocumentRepository;
import com.synapsecode.accountservice.service.AccountService;
import com.synapsecode.accountservice.service.AuditService;
import com.synapsecode.accountservice.service.KycValidationService;
import com.synapsecode.accountservice.service.NotificationService;
import com.synapsecode.accountservice.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ContactDetailsRepository contactDetailsRepository;
    private final AddressRepository addressRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AuditService auditService;
    private final KycValidationService kycValidationService;
    private final NotificationService notificationService;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreationRequest request) {
        log.info("Creating new account for customer: {}", request.customerId());

        // Check if minimum required data is present
        if (request.addresses() == null || request.addresses().isEmpty() ||
                request.kycDocuments() == null || request.kycDocuments().isEmpty()) {
            throw new InsufficientDataException("Address and KYC documents are required");
        }

        // Create contact details
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setEmail(request.email());
        contactDetails.setPhoneNumber(request.phoneNumber());
        contactDetails.setAlternatePhoneNumber(request.alternatePhoneNumber());
        contactDetails.setIsEmailVerified(false);
        contactDetails.setIsPhoneVerified(false);
        contactDetails.setPreferredContactMethod("EMAIL");

        // Create account preferences (with defaults if not provided)
        AccountPreference preferences = createAccountPreferences(request.preferences());

        // Generate account number
        String accountNumber = accountNumberGenerator.generateAccountNumber();

        // Create account
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountHolderName(request.accountHolderName());
        account.setAccountType(request.accountType());
        account.setBranchCode(request.branchCode());
        account.setCurrentBalance(request.initialDeposit() != null ? request.initialDeposit() : BigDecimal.ZERO);
        account.setAvailableBalance(request.initialDeposit() != null ? request.initialDeposit() : BigDecimal.ZERO);
        account.setMinimumBalance(request.minimumBalance() != null ? request.minimumBalance() : BigDecimal.ZERO);
        account.setOverdraftLimit(request.overdraftLimit() != null ? request.overdraftLimit() : BigDecimal.ZERO);
        account.setIsOverdraftEnabled(request.isOverdraftEnabled() != null ? request.isOverdraftEnabled() : false);
        account.setCurrency(request.currency());
        account.setDateOfBirth(request.dateOfBirth());
        account.setNationality(request.nationality());
        account.setTaxIdNumber(request.taxIdNumber());
        account.setCustomerId(request.customerId());
        account.setStatus(AccountStatus.KYC_PENDING);
        account.setContactDetails(contactDetails);
        account.setPreferences(preferences);
        account.setOpeningDate(LocalDateTime.now());
        account.setRiskRating("MEDIUM"); // Default risk rating

        contactDetails.setAccount(account);
        preferences.setAccount(account);

        Account savedAccount = accountRepository.save(account);

        // Add addresses
        for (AccountCreationRequest.AddressRequest addressRequest : request.addresses()) {
            Address address = new Address();
            address.setAddressType(addressRequest.addressType());
            address.setAddressLine1(addressRequest.addressLine1());
            address.setAddressLine2(addressRequest.addressLine2());
            address.setCity(addressRequest.city());
            address.setState(addressRequest.state());
            address.setPostalCode(addressRequest.postalCode());
            address.setCountry(addressRequest.country());
            address.setIsPrimary(addressRequest.isPrimary() != null ? addressRequest.isPrimary() : false);
            address.setIsVerified(false);
            address.setAccount(savedAccount);

            addressRepository.save(address);
        }

        // Add KYC documents
        for (AccountCreationRequest.KycDocumentRequest kycRequest : request.kycDocuments()) {
            KycDocument document = new KycDocument();
            document.setDocumentType(kycRequest.documentType());
            document.setDocumentNumber(kycRequest.documentNumber());
            document.setIssuingAuthority(kycRequest.issuingAuthority());
            document.setIssueDate(kycRequest.issueDate());
            document.setExpiryDate(kycRequest.expiryDate());
            document.setDocumentPath(kycRequest.documentPath());
            document.setVerificationStatus("PENDING");
            document.setAccount(savedAccount);

            kycDocumentRepository.save(document);
        }

        // Record audit
        auditService.recordAccountCreation(savedAccount);

        // Send notification
        notificationService.sendAccountCreationNotification(savedAccount);

        // Start KYC validation process asynchronously
        kycValidationService.startValidation(savedAccount.getId());

        return mapToAccountResponse(savedAccount);
    }

    @Override
    public AccountResponse getAccountById(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        return mapToAccountResponse(account);
    }

    @Override
    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with number: " + accountNumber));

        return mapToAccountResponse(account);
    }

    @Override
    public Page<AccountResponse> getAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(this::mapToAccountResponse);
    }

    @Override
    public List<AccountResponse> getAccountsByCustomerId(UUID customerId) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AccountResponse> getAccountsByCustomerId(UUID customerId, Pageable pageable) {
        return accountRepository.findByCustomerId(customerId, pageable)
                .map(this::mapToAccountResponse);
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(UUID accountId, AccountUpdateRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        // Update account details if provided
        if (request.accountHolderName() != null) {
            account.setAccountHolderName(request.accountHolderName());
        }

        if (request.minimumBalance() != null) {
            account.setMinimumBalance(request.minimumBalance());
        }

        if (request.overdraftLimit() != null) {
            account.setOverdraftLimit(request.overdraftLimit());
        }

        if (request.isOverdraftEnabled() != null) {
            account.setIsOverdraftEnabled(request.isOverdraftEnabled());
        }

        // Update contact details if provided
        ContactDetails contactDetails = account.getContactDetails();
        boolean contactDetailsUpdated = false;

        if (request.email() != null) {
            contactDetails.setEmail(request.email());
            contactDetails.setIsEmailVerified(false); // Reset verification as email changed
            contactDetailsUpdated = true;
        }

        if (request.phoneNumber() != null) {
            contactDetails.setPhoneNumber(request.phoneNumber());
            contactDetails.setIsPhoneVerified(false); // Reset verification as phone changed
            contactDetailsUpdated = true;
        }

        if (request.alternatePhoneNumber() != null) {
            contactDetails.setAlternatePhoneNumber(request.alternatePhoneNumber());
            contactDetailsUpdated = true;
        }

        if (request.preferredContactMethod() != null) {
            contactDetails.setPreferredContactMethod(request.preferredContactMethod());
            contactDetailsUpdated = true;
        }

        // Update addresses if provided
        if (request.addresses() != null && !request.addresses().isEmpty()) {
            // Get existing addresses
            List<Address> existingAddresses = addressRepository.findByAccountId(accountId);

            // Delete existing addresses
            for (Address existingAddress : existingAddresses) {
                addressRepository.delete(existingAddress);
            }

            // Add new addresses
            for (AccountCreationRequest.AddressRequest addressRequest : request.addresses()) {
                Address address = new Address();
                address.setAddressType(addressRequest.addressType());
                address.setAddressLine1(addressRequest.addressLine1());
                address.setAddressLine2(addressRequest.addressLine2());
                address.setCity(addressRequest.city());
                address.setState(addressRequest.state());
                address.setPostalCode(addressRequest.postalCode());
                address.setCountry(addressRequest.country());
                address.setIsPrimary(addressRequest.isPrimary() != null ? addressRequest.isPrimary() : false);
                address.setIsVerified(false);
                address.setAccount(account);

                addressRepository.save(address);
            }
        }

        // Update preferences if provided
        if (request.preferences() != null) {
            AccountPreference preferences = account.getPreferences();
            if (preferences == null) {
                preferences = createAccountPreferences(request.preferences());
                preferences.setAccount(account);
                account.setPreferences(preferences);
            } else {
                updateAccountPreferences(preferences, request.preferences());
            }
        }

        Account updatedAccount = accountRepository.save(account);

        // Record audit
        auditService.recordAccountUpdate(updatedAccount);

        // Send notification if contact details were updated
        if (contactDetailsUpdated) {
//            Needs to implement a new method for this
//            notificationService.sendContactDetailsUpdateNotification(updatedAccount);
        }

        return mapToAccountResponse(updatedAccount);
    }

    @Override
    @Transactional
    public void updateAccountStatus(UUID accountId, AccountStatus status, String reason) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        AccountStatus oldStatus = account.getStatus();
        account.setStatus(status);

        if (status == AccountStatus.ACTIVE) {
            account.setLastActiveDate(LocalDateTime.now());
        }

        accountRepository.save(account);

        // Record audit
        auditService.recordAccountStatusChange(account, oldStatus);

        // Send notification
        notificationService.sendAccountStatusChangeNotification(account, oldStatus);
    }

    @Override
    @Transactional
    public void closeAccount(UUID accountId, String reason) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        if (account.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Cannot close account with positive balance");
        }

        AccountStatus oldStatus = account.getStatus();
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedDate(LocalDateTime.now());
        account.setClosureReason(reason);

        accountRepository.save(account);

        // Record audit
        auditService.recordAccountClosure(account);

        // Send notification
        notificationService.sendAccountClosureNotification(account);
    }

    @Override
    public BalanceResponse getAccountBalance(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        return buildBalanceResponse(account);
    }

    @Override
    public BalanceResponse getAccountBalanceByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with number: " + accountNumber));

        return buildBalanceResponse(account);
    }

    @Override
    public List<TransactionResponse> getAccountTransactions(UUID accountId, Pageable pageable) {
        // First verify the account exists
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        // This would be fetched from a Transaction service in a real microservice architecture
        // For this implementation, we'll return a mock response
        String accountNumber = account.getAccountNumber();

        // In a real implementation, this would call the Transaction microservice via REST or messaging
        // String transactionServiceUrl = "http://transaction-service/api/accounts/" + accountNumber + "/transactions";
        // TransactionResponse[] transactions = restTemplate.getForObject(transactionServiceUrl, TransactionResponse[].class);

        // For now, returning mock data
        List<TransactionResponse> mockTransactions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        mockTransactions.add(new TransactionResponse(
                UUID.randomUUID(), "DEPOSIT", new BigDecimal("1000.00"),
                "Initial deposit", now.minusDays(30), "REF123456",
                "COMPLETED", new BigDecimal("1000.00"), account.getCurrency()));

        mockTransactions.add(new TransactionResponse(
                UUID.randomUUID(), "WITHDRAWAL", new BigDecimal("200.00"),
                "ATM withdrawal", now.minusDays(25), "REF123457",
                "COMPLETED", new BigDecimal("800.00"), account.getCurrency()));

        mockTransactions.add(new TransactionResponse(
                UUID.randomUUID(), "TRANSFER", new BigDecimal("300.00"),
                "Transfer to Jane", now.minusDays(20), "REF123458",
                "COMPLETED", new BigDecimal("500.00"), account.getCurrency()));

        // Return the list
        return mockTransactions;
    }

    @Override
    @Transactional
    public void validateKycDocuments(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        List<KycDocument> documents = kycDocumentRepository.findByAccountId(accountId);

        if (documents.isEmpty()) {
            throw new KycValidationException("No KYC documents found for account");
        }

        boolean allValid = true;
        for (KycDocument document : documents) {
            // In a real implementation, this would involve a more comprehensive validation process
            // For now, just checking if the documents are expired
            if (document.getExpiryDate().isBefore(java.time.LocalDate.now())) {
                document.setVerificationStatus("REJECTED");
                document.setVerificationComments("Document expired");
                kycDocumentRepository.save(document);
                allValid = false;
            } else {
                document.setVerificationStatus("VERIFIED");
                document.setVerificationDate(LocalDateTime.now());
                document.setVerifiedBy("SYSTEM");
                kycDocumentRepository.save(document);
            }
        }

        if (allValid) {
            // Update account status
            account.setStatus(AccountStatus.ACTIVE);
            accountRepository.save(account);

            // Send notification
            notificationService.sendKycUpdateNotification(account);
        } else {
            // Send notification about failed verification
            notificationService.sendKycUpdateNotification(account);
            throw new KycValidationException("KYC validation failed - some documents are invalid");
        }
    }

    @Override
    @Transactional
    public BalanceResponse updateAccountBalance(UUID accountId, BigDecimal amount, String transactionReference) {
        log.info("Updating balance for account ID: {} by amount: {} (Ref: {})", accountId, amount, transactionReference);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot update balance for closed account: " + accountId);
        }

        if (account.getStatus() == AccountStatus.SUSPENDED && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Cannot debit suspended account: " + accountId);
        }

        BigDecimal previousBalance = account.getAvailableBalance();
        BigDecimal newBalance = previousBalance.add(amount);

        // Check for sufficient funds when debiting
        if (amount.compareTo(BigDecimal.ZERO) < 0 && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            if (!account.getIsOverdraftEnabled() ||
                    newBalance.abs().compareTo(account.getOverdraftLimit()) > 0) {
                throw new IllegalStateException("Insufficient funds in account: " + accountId);
            }
        }

        account.setCurrentBalance(newBalance);
        account.setAvailableBalance(calculateAvailableBalance(account));
        account.setLastModifiedDate(LocalDateTime.now());
        account.setTransactionReference(Collections.singletonList(transactionReference));

        Account updatedAccount = accountRepository.save(account);

        // Audit balance change
        auditService.recordBalanceChange(updatedAccount, previousBalance, newBalance);

        // Send balance alert notification if applicable
        notificationService.sendBalanceAlertNotification(updatedAccount, previousBalance, newBalance);

        log.info("Balance updated successfully for account: {}, New balance: {}",
                account.getAccountNumber(), newBalance);

        return getAccountBalance(accountId);
    }

    private BigDecimal calculateAvailableBalance(Account account) {
        BigDecimal availableBalance = account.getAvailableBalance();

        if (account.getIsOverdraftEnabled()) {
            availableBalance = availableBalance.add(account.getOverdraftLimit());
        }

        return availableBalance;
    }

    @Override
    @Transactional
    public AccountResponse updateKycStatus(UUID accountId, UUID documentId, String kycStatus) {
        log.info("Updating KYC status for account ID: {} to: {}", accountId, kycStatus);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

//        account.getKycDocuments().stream().map( kycDocument -> kycDocument.getDocumentNumber()).
        KycDocument kycDocument = account.getKycDocuments().stream()
                        .filter(doc -> doc.getId().equals(documentId))
                                .findFirst()
                                        .orElseThrow(()-> new ResourceAccessException("Kyc not found"));
//        account.setKycStatus(kycStatus);
        // Update the KYC document's status
        kycDocument.setVerificationStatus(kycStatus);
        kycDocument.setVerificationDate(LocalDateTime.now());
        kycDocument.setVerifiedBy("SYSTEM");

        // Save the updated account
        account.setLastModifiedDate(LocalDateTime.now());

        Account updatedAccount = accountRepository.save(account);

        // Audit KYC update
        auditService.recordKycUpdate(updatedAccount);

        // Send notification
        notificationService.sendKycUpdateNotification(updatedAccount);

        log.info("KYC status updated successfully for account: {}", account.getAccountNumber());
        return mapToAccountResponse(updatedAccount);
    }

    @Override
    public boolean accountExists(UUID accountId) {
        return accountRepository.existsById(accountId);
    }

    @Override
    public boolean accountExistsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }

    // Helper methods
    private AccountPreference createAccountPreferences(AccountCreationRequest.AccountPreferenceRequest request) {
        AccountPreference preferences = new AccountPreference();

        if (request != null) {
            preferences.setStatementFrequency(request.statementFrequency() != null ?
                    request.statementFrequency() : "MONTHLY");
            preferences.setStatementDeliveryMode(request.statementDeliveryMode() != null ?
                    request.statementDeliveryMode() : "EMAIL");
            preferences.setNotificationPreference(request.notificationPreference() != null ?
                    request.notificationPreference() : true);
            preferences.setTransactionAlerts(request.transactionAlerts() != null ?
                    request.transactionAlerts() : true);
            preferences.setBalanceAlerts(request.balanceAlerts() != null ?
                    request.balanceAlerts() : true);
            preferences.setMarketingCommunications(request.marketingCommunications() != null ?
                    request.marketingCommunications() : false);
            preferences.setThresholdAlertAmount(request.thresholdAlertAmount() != null ?
                    request.thresholdAlertAmount() : 100.0);
            preferences.setPreferredLanguage(request.preferredLanguage() != null ?
                    request.preferredLanguage() : "ENGLISH");
        } else {
            // Set defaults
            preferences.setStatementFrequency("MONTHLY");
            preferences.setStatementDeliveryMode("EMAIL");
            preferences.setNotificationPreference(true);
            preferences.setTransactionAlerts(true);
            preferences.setBalanceAlerts(true);
            preferences.setMarketingCommunications(false);
            preferences.setThresholdAlertAmount(100.0);
            preferences.setPreferredLanguage("ENGLISH");
        }

        return preferences;
    }

    private void updateAccountPreferences(AccountPreference preferences,
                                          AccountCreationRequest.AccountPreferenceRequest request) {
        if (request.statementFrequency() != null) {
            preferences.setStatementFrequency(request.statementFrequency());
        }

        if (request.statementDeliveryMode() != null) {
            preferences.setStatementDeliveryMode(request.statementDeliveryMode());
        }

        if (request.notificationPreference() != null) {
            preferences.setNotificationPreference(request.notificationPreference());
        }

        if (request.transactionAlerts() != null) {
            preferences.setTransactionAlerts(request.transactionAlerts());
        }

        if (request.balanceAlerts() != null) {
            preferences.setBalanceAlerts(request.balanceAlerts());
        }

        if (request.marketingCommunications() != null) {
            preferences.setMarketingCommunications(request.notificationPreference());
        }

        if (request.thresholdAlertAmount() != null) {
            preferences.setThresholdAlertAmount(request.thresholdAlertAmount());
        }

        if (request.preferredLanguage() != null) {
            preferences.setPreferredLanguage(request.preferredLanguage());
        }
    }

    private AccountResponse mapToAccountResponse(Account account) {
        // Load addresses and kyc documents from their repositories
        List<Address> addresses = addressRepository.findByAccountId(account.getId());
        List<KycDocument> kycDocuments = kycDocumentRepository.findByAccountId(account.getId());

        // Map contact details
        AccountResponse.ContactDetailsResponse contactDetailsResponse = null;
        if (account.getContactDetails() != null) {
            contactDetailsResponse = AccountResponse.ContactDetailsResponse.builder()
                    .email(account.getContactDetails().getEmail())
                    .phoneNumber(account.getContactDetails().getPhoneNumber())
                    .alternatePhoneNumber(account.getContactDetails().getAlternatePhoneNumber())
                    .isEmailVerified(account.getContactDetails().getIsEmailVerified())
                    .isPhoneVerified(account.getContactDetails().getIsPhoneVerified())
                    .preferredContactMethod(account.getContactDetails().getPreferredContactMethod())
                    .build();
        }

        // Map addresses
        List<AccountResponse.AddressResponse> addressResponses = addresses.stream()
                .map(address -> AccountResponse.AddressResponse.builder()
                        .id(address.getId())
                        .addressType(address.getAddressType())
                        .addressLine1(address.getAddressLine1())
                        .addressLine2(address.getAddressLine2())
                        .city(address.getCity())
                        .state(address.getState())
                        .postalCode(address.getPostalCode())
                        .country(address.getCountry())
                        .isPrimary(address.getIsPrimary())
                        .isVerified(address.getIsVerified())
                        .build())
                .collect(Collectors.toList());

        // Map KYC documents
        List<AccountResponse.KycDocumentResponse> kycResponses = kycDocuments.stream()
                .map(doc -> AccountResponse.KycDocumentResponse.builder()
                        .id(doc.getId())
                        .documentType(doc.getDocumentType())
                        .documentNumber(doc.getDocumentNumber())
                        .issuingAuthority(doc.getIssuingAuthority())
                        .issueDate(doc.getIssueDate())
                        .expiryDate(doc.getExpiryDate())
                        .verificationStatus(doc.getVerificationStatus())
                        .build())
                .collect(Collectors.toList());

        // Map preferences
        AccountResponse.AccountPreferenceResponse preferencesResponse = null;
        if (account.getPreferences() != null) {
            preferencesResponse = AccountResponse.AccountPreferenceResponse.builder()
                    .statementFrequency(account.getPreferences().getStatementFrequency())
                    .statementDeliveryMode(account.getPreferences().getStatementDeliveryMode())
                    .notificationPreference(account.getPreferences().getNotificationPreference())
                    .transactionAlerts(account.getPreferences().getTransactionAlerts())
                    .balanceAlerts(account.getPreferences().getBalanceAlerts())
                    .marketingCommunications(account.getPreferences().getMarketingCommunications())
                    .thresholdAlertAmount(account.getPreferences().getThresholdAlertAmount())
                    .preferredLanguage(account.getPreferences().getPreferredLanguage())
                    .build();
        }

        // Build and return account response
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountHolderName(account.getAccountHolderName())
                .status(account.getStatus())
                .accountType(account.getAccountType())
                .branchCode(account.getBranchCode())
                .currentBalance(account.getCurrentBalance())
                .availableBalance(account.getAvailableBalance())
                .minimumBalance(account.getMinimumBalance())
                .overdraftLimit(account.getOverdraftLimit())
                .isOverdraftEnabled(account.getIsOverdraftEnabled())
                .currency(account.getCurrency())
                .openingDate(account.getOpeningDate())
                .lastActiveDate(account.getLastActiveDate())
                .contactDetails(contactDetailsResponse)
                .addresses(addressResponses)
                .kycDocuments(kycResponses)
                .preferences(preferencesResponse)
                .build();
    }

    private BalanceResponse buildBalanceResponse(Account account) {
        BigDecimal availableOverdraft = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(account.getIsOverdraftEnabled())) {
            availableOverdraft = account.getOverdraftLimit();
        }

        return BalanceResponse.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .currentBalance(account.getCurrentBalance())
                .availableBalance(account.getAvailableBalance())
                .minimumBalance(account.getMinimumBalance())
                .overdraftLimit(account.getOverdraftLimit())
                .availableOverdraft(availableOverdraft)
                .currency(account.getCurrency())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}