package com.campus.banking.service;


import com.campus.banking.model.BankAccount;

public interface BankAccountService<T extends BankAccount> {

    void add(T account);

    T getByAccountNumber(String accountNumber);

    void deposit(String accountNumber, double amount);

    void withdraw(String accountNumber, double amount);

    double sumBalanceHigherThan(double min);
}
