package com.synapsecode.accountservice.exception;

public class InsufficientDataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InsufficientDataException(String message) {
        super(message);
    }

    public InsufficientDataException(String message, Throwable cause) {
        super(message, cause);
    }
}