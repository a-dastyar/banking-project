package com.campus.banking.service;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.model.BankAccount;

public class BankAccountServiceImpl implements BankAccountService {

    @Override
    public void deposit(BankAccount account, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not deposit negative amount");
        }

        try (var lock = account.getLock().lock()) {
            account.setBalance(account.getBalance() + amount);
        }
    }

    @Override
    public void withdraw(BankAccount account, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not withdraw negative amount");
        }
        try (var lock = account.getLock().lock()) {
            if (amount > account.getBalance()) {
                throw new InsufficientFundsException();
            }
            account.setBalance(account.getBalance() - amount);
        }
    }

}
