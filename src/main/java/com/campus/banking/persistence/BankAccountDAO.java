package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;

import com.campus.banking.model.BankAccount;

public interface BankAccountDAO {

    void add(BankAccount account);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    void removeByAccountNumber(String accountNumber);

    List<BankAccount> list();
}
