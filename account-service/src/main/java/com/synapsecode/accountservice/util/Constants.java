package com.synapsecode.accountservice.util;

public class Constants {

    // Account Types
    public static final String ACCOUNT_TYPE_SAVINGS = "SAVINGS";
    public static final String ACCOUNT_TYPE_CURRENT = "CURRENT";
    public static final String ACCOUNT_TYPE_FIXED_DEPOSIT = "FIXED_DEPOSIT";
    public static final String ACCOUNT_TYPE_LOAN = "LOAN";

    // Currencies
    public static final String CURRENCY_USD = "USD";
    public static final String CURRENCY_EUR = "EUR";
    public static final String CURRENCY_GBP = "GBP";
    public static final String CURRENCY_INR = "INR";

    // KYC Status
    public static final String KYC_STATUS_PENDING = "PENDING";
    public static final String KYC_STATUS_VERIFIED = "VERIFIED";
    public static final String KYC_STATUS_REJECTED = "REJECTED";
    public static final String KYC_STATUS_EXPIRED = "EXPIRED";

    // Document Types
    public static final String DOCUMENT_TYPE_IDENTITY = "IDENTITY_PROOF";
    public static final String DOCUMENT_TYPE_ADDRESS = "ADDRESS_PROOF";
    public static final String DOCUMENT_TYPE_INCOME = "INCOME_PROOF";

    // Statement Frequency
    public static final String STATEMENT_FREQUENCY_MONTHLY = "MONTHLY";
    public static final String STATEMENT_FREQUENCY_QUARTERLY = "QUARTERLY";
    public static final String STATEMENT_FREQUENCY_ANNUALLY = "ANNUALLY";

    // Notification Preferences
    public static final String NOTIFICATION_PREFERENCE_EMAIL = "EMAIL";
    public static final String NOTIFICATION_PREFERENCE_SMS = "SMS";
    public static final String NOTIFICATION_PREFERENCE_BOTH = "BOTH";
    public static final String NOTIFICATION_PREFERENCE_NONE = "NONE";

    // Pagination defaults
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    // Error Messages
    public static final String ERROR_ACCOUNT_NOT_FOUND = "Account not found";
    public static final String ERROR_CUSTOMER_ID_REQUIRED = "Customer ID is required";
    public static final String ERROR_ACCOUNT_TYPE_REQUIRED = "Account type is required";
    public static final String ERROR_CURRENCY_REQUIRED = "Currency is required";
    public static final String ERROR_KYC_REQUIRED = "KYC documents are required";

    // Audit Event Types
    public static final String AUDIT_EVENT_ACCOUNT_CREATION = "ACCOUNT_CREATION";
    public static final String AUDIT_EVENT_ACCOUNT_UPDATE = "ACCOUNT_UPDATE";
    public static final String AUDIT_EVENT_ACCOUNT_STATUS_CHANGE = "ACCOUNT_STATUS_CHANGE";
    public static final String AUDIT_EVENT_ACCOUNT_CLOSURE = "ACCOUNT_CLOSURE";
    public static final String AUDIT_EVENT_BALANCE_CHANGE = "BALANCE_CHANGE";
    public static final String AUDIT_EVENT_KYC_UPDATE = "KYC_UPDATE";

    // Kafka Topics
    public static final String TOPIC_ACCOUNT_EVENTS = "account-events";
    public static final String TOPIC_ACCOUNT_AUDIT_EVENTS = "account-audit-events";
    public static final String TOPIC_NOTIFICATION_EVENTS = "notification-events";

    private Constants() {
        // Private constructor to prevent instantiation
    }
}