package com.synapsecode.accountservice.exception;

public class DuplicateAccountException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DuplicateAccountException(String message) {
        super(message);
    }

    public DuplicateAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateAccountException(String identifierType, String identifier) {
        super(String.format("Account with %s: %s already exists", identifierType, identifier));
    }
}