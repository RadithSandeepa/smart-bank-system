package com.synapsecode.accountservice.service;

import com.synapsecode.accountservice.dto.request.AccountCreationRequest;
import com.synapsecode.accountservice.dto.request.AccountUpdateRequest;
import com.synapsecode.accountservice.dto.response.AccountResponse;
import com.synapsecode.accountservice.dto.response.BalanceResponse;
import com.synapsecode.accountservice.dto.response.TransactionResponse;
import com.synapsecode.accountservice.entity.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountResponse createAccount(AccountCreationRequest request);

    AccountResponse getAccountById(UUID accountId);

    AccountResponse getAccountByNumber(String accountNumber);

    Page<AccountResponse> getAccounts(Pageable pageable);

    List<AccountResponse> getAccountsByCustomerId(UUID customerId);

    Page<AccountResponse> getAccountsByCustomerId(UUID customerId, Pageable pageable);

    AccountResponse updateAccount(UUID accountId, AccountUpdateRequest request);

    void updateAccountStatus(UUID accountId, AccountStatus status, String reason);

    void closeAccount(UUID accountId, String reason);

    BalanceResponse getAccountBalance(UUID accountId);

    BalanceResponse getAccountBalanceByNumber(String accountNumber);

    List<TransactionResponse> getAccountTransactions(UUID accountId, Pageable pageable);

    void validateKycDocuments(UUID accountId);

    @Transactional
    BalanceResponse updateAccountBalance(UUID accountId, BigDecimal amount, String transactionReference);

    @Transactional
    AccountResponse updateKycStatus(UUID accountId, UUID documentId, String kycStatus);

    boolean accountExists(UUID accountId);

    boolean accountExistsByAccountNumber(String accountNumber);
}