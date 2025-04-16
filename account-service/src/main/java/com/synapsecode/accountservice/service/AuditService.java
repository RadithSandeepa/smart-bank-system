package com.synapsecode.accountservice.service;

import com.synapsecode.accountservice.entity.Account;
import com.synapsecode.accountservice.entity.AccountStatus;

import java.math.BigDecimal;

public interface AuditService {
    void recordAccountCreation(Account account);

    void recordAccountUpdate(Account account);

    void recordAccountStatusChange(Account account, AccountStatus previousStatus);

    void recordAccountClosure(Account account);

    void recordBalanceChange(Account account, BigDecimal previousBalance, BigDecimal newBalance);

    void recordKycUpdate(Account account);
}
