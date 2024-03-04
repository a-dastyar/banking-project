package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;

import com.campus.banking.model.BankAccount;

public interface BankAccountDAO<T extends BankAccount> {

    void add(T account);

    Optional<T> findByAccountNumber(String accountNumber);

    void removeByAccountNumber(String accountNumber);

    List<T> list();
}