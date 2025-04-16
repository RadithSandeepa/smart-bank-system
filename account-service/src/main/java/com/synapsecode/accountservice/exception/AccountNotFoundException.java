package com.synapsecode.accountservice.exception;


public class AccountNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountNotFoundException(Long accountId) {
        super(String.format("Account with ID %d not found", accountId));
    }
}