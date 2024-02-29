package com.campus.banking.service;

import java.util.List;
import java.util.concurrent.Executors;

import com.campus.banking.exception.InvalidAccountTypeException;
import com.campus.banking.exception.InvalidTransactionException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.SavingAccount;

public class SavingAccountServiceImpl extends BankAccountServiceImpl implements SavingAccountService {
    @Override
    public void withdraw(BankAccount account, double amount) {
        if (!(account instanceof SavingAccount)) {
            throw new InvalidAccountTypeException("BankAccount type must be from type SavingAccount");
        }

        SavingAccount savingAccount = (SavingAccount) account;

        try (var lock = savingAccount.getLock().lock()) {
            double maximum_withdraw = savingAccount.getBalance() - savingAccount.getMinimumBalance();
            if (amount > maximum_withdraw) {
                throw new InvalidTransactionException("Can not withdraw more than " + maximum_withdraw);
            }

            super.withdraw(savingAccount, amount);
        }
    }

    @Override
    public void deposit(BankAccount account, double amount) {
        if (!(account instanceof SavingAccount)) {
            throw new InvalidAccountTypeException("BankAccount type must be from type SavingAccount");
        }
        super.deposit(account, amount);
    }

    @Override
    public void applyInterest(SavingAccount account) {

        try (var lock = account.getLock().lock()) {
            double interest = account.getBalance() * SavingAccount.INTEREST_RATE;
            
            super.deposit(account, interest);
        }
    }

    @Override
    public void applyInterest(List<SavingAccount> accounts) {
        try (var executors = Executors.newVirtualThreadPerTaskExecutor()) {
            for (var account : accounts) {
                executors.submit(() -> this.applyInterest(account));
            }
        }
    }
}
