package com.campus.banking.service;


import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.model.BankAccount;

public class BankAccountServiceImpl<T extends BankAccount> implements BankAccountService<T> {

    @Override
    public void deposit(T account, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not deposit negative amount");
        }
        account.setBalance(account.getBalance() + amount);
    }

    @Override
    public void withdraw(T account, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not withdraw negative amount");
        }
        if (amount > account.getBalance()) {
            throw new InsufficientFundsException();
        }
        account.setBalance(account.getBalance() - amount);
    }

}
