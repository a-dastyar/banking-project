package com.campus.banking.service;

import java.util.List;
import java.util.function.Predicate;

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

    @Override
    public double sumBalance(List<T> accounts, Predicate<T> predicate) {
        return accounts.stream()
                .filter(predicate)
                .mapToDouble(BankAccount::getBalance)
                .sum();
    }

}
