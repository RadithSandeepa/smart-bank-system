package com.synapsecode.accountservice.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AccountNumberGenerator {

    private static final String BRANCH_CODE = "0001";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a unique account number
     * Format: BRANCHCODE (4) + TIMESTAMP (6) + RANDOM (6)
     * Example: 0001-202504-123456
     *
     * @return Unique account number
     */
    public String generateAccountNumber() {
        // Get current timestamp in format yyMMdd
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

        // Generate random 6-digit number
        int randomNum = RANDOM.nextInt(900000) + 100000; // 100000 to 999999

        // Combine to create unique account number
        return BRANCH_CODE + "-" + timestamp + "-" + randomNum;
    }
}