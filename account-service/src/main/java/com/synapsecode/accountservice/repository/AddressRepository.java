package com.synapsecode.accountservice.repository;


import com.synapsecode.accountservice.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByAccountId(UUID accountId);

    void deleteByAccountId(UUID accountId);
}