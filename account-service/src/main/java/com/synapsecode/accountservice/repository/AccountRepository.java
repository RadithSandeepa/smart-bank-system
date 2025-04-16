package com.synapsecode.accountservice.repository;


import com.synapsecode.accountservice.entity.Account;
import com.synapsecode.accountservice.entity.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(UUID customerId);

    Page<Account> findByCustomerId(UUID customerId, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.status = :status")
    List<Account> findByStatus(AccountStatus status);

    @Query("SELECT a FROM Account a WHERE a.lastActiveDate < :date AND a.status = :status")
    List<Account> findInactiveAccounts(LocalDateTime date, AccountStatus status);

    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT a FROM Account a JOIN a.contactDetails c WHERE c.email = :email")
    List<Account> findByEmail(String email);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.status = :status")
    Long countAccountsByStatus(AccountStatus status);
}
