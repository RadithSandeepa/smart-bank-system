package com.synapsecode.accountservice.exception;

public class KycValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String documentType;

    public KycValidationException(String message) {
        super(message);
    }

    public KycValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public KycValidationException(String documentType, String message) {
        super(message);
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return documentType;
    }
}