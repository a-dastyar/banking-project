package com.campus.banking.service;

import com.campus.banking.model.BankAccount;

public interface BankAccountService {
    void deposit(BankAccount account, double amount);

    void withdraw(BankAccount account, double amount);
}
