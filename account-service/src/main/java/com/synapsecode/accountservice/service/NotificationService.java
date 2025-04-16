package com.synapsecode.accountservice.service;

import com.synapsecode.accountservice.entity.Account;
import com.synapsecode.accountservice.entity.AccountStatus;

import java.math.BigDecimal;

public interface NotificationService {
    void sendAccountCreationNotification(Account account);

    void sendAccountStatusChangeNotification(Account account, AccountStatus previousStatus);

    void sendAccountClosureNotification(Account account);

    void sendBalanceAlertNotification(Account account, BigDecimal previousBalance, BigDecimal newBalance);

    void sendKycUpdateNotification(Account account);
}
