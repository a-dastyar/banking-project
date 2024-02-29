package com.campus.banking.service;

import com.campus.banking.model.BankAccount;

public interface BankAccountService<T extends BankAccount> {
    void deposit(T account, double amount);

    void withdraw(T account, double amount);
}
