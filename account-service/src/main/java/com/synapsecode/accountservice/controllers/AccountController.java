package com.synapsecode.accountservice.controllers;

import com.synapsecode.accountservice.dto.request.AccountCreationRequest;
import com.synapsecode.accountservice.dto.request.AccountUpdateRequest;
import com.synapsecode.accountservice.dto.response.AccountResponse;
import com.synapsecode.accountservice.dto.response.BalanceResponse;
import com.synapsecode.accountservice.entity.AccountStatus;
import com.synapsecode.accountservice.listener.KycValidationEventListener;
import com.synapsecode.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/sample")
    public String sampleGet(){
        return "Endpoint working";
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountCreationRequest request) {
        log.info("REST request to create account for customer: {}", request.customerId());
        AccountResponse response = accountService.createAccount(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable UUID accountId,
            @Valid @RequestBody AccountUpdateRequest request) {
        log.info("REST request to update account: {}", accountId);
        AccountResponse response = accountService.updateAccount(accountId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccounts(
            @RequestParam UUID customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get accounts for customer: {}", customerId);
        List<AccountResponse> accounts = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID accountId) {
        log.info("REST request to get account: {}", accountId);
        AccountResponse account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<BalanceResponse> getAccountByNumber(@PathVariable String accountNumber) {
        log.info("REST request to get account by number: {}", accountNumber);
        BalanceResponse account = accountService.getAccountBalanceByNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{accountId}/status")
    public ResponseEntity<String> changeAccountStatus(
            @PathVariable UUID accountId,
            @RequestParam AccountStatus status,
            @RequestParam(required = false) String reason) {
        log.info("REST request to change status of account: {} to {}", accountId, status);
        accountService.updateAccountStatus(accountId, status, reason);
        return ResponseEntity.ok("Account update success");
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<String> closeAccount(
            @PathVariable UUID accountId,
            @RequestParam(required = false) String reason) {
        log.info("REST request to close account: {}", accountId);
        accountService.closeAccount(accountId, reason);
        return ResponseEntity.ok("Account close successfull");
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BalanceResponse> getAccountBalance(@PathVariable UUID accountId) {
        log.info("REST request to get balance for account: {}", accountId);
        BalanceResponse balance = accountService.getAccountBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @PutMapping("/{accountId}/balance")
    public ResponseEntity<BalanceResponse> updateAccountBalance(
            @PathVariable UUID accountId,
            @RequestParam BigDecimal amount,
            @RequestParam String transactionReference) {
        log.info("REST request to update balance for account: {} by amount: {}", accountId, amount);
        BalanceResponse balance = accountService.updateAccountBalance(accountId, amount, transactionReference);
        return ResponseEntity.ok(balance);
    }

    @PutMapping("/{accountId}/kyc")
    public ResponseEntity<AccountResponse> updateKycStatus(
            @PathVariable UUID accountId,
            @PathVariable UUID documentId,
            @RequestParam String kycStatus) {
        log.info("REST request to update KYC status for account: {} to {}", accountId, kycStatus);
        AccountResponse response = accountService.updateKycStatus(accountId, documentId, kycStatus);
        return ResponseEntity.ok(response);
    }
}