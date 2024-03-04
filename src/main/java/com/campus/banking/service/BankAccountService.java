package com.campus.banking.service;

import java.util.List;
import java.util.function.Predicate;

import com.campus.banking.model.BankAccount;

public interface BankAccountService<T extends BankAccount> {
    void deposit(T account, double amount);

    void withdraw(T account, double amount);

    double sumBalance(List<T> accounts, Predicate<T> predicate);
}
