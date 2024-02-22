package com.campus.banking.service;


import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.model.BankAccount;

public class BankAccountServiceImpl implements BankAccountService {

    @Override
    public void deposit(BankAccount account, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not deposit negative amount");
        }
        account.setAmount(account.getAmount() + amount);
    }

    @Override
    public void withdraw(BankAccount account, double amount) {
        if (amount < 0 || amount > account.getAmount()) {
            throw new InsufficientFundsException("Can not deposit negative amount");
        }
        account.setAmount(account.getAmount() - amount);
    }

}
