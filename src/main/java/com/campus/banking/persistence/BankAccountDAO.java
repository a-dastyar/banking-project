package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;

import com.campus.banking.model.BankAccount;

import jakarta.persistence.EntityManager;

public interface BankAccountDAO<T extends BankAccount> extends DAO<T, Long> {

    Optional<T> findByAccountNumber(String accountNumber);

    List<T> findByUsername(String username);

    Optional<T>  findByAccountNumberForUpdate(EntityManager em, String accountNumber);

    double sumBalanceHigherThan(double min);
}
