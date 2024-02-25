package com.campus.banking.service;

import com.campus.banking.exception.InvalidAccountTypeException;
import com.campus.banking.exception.InvalidTransactionException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.SavingAccount;

public class SavingAccountService extends BankAccountServiceImpl {
    @Override
    public void deposit(BankAccount account, double amount) {
        super.deposit(account, amount);
    }

    @Override
    public void withdraw(BankAccount account, double amount) {
        if (!(account instanceof SavingAccount)) {
            throw new InvalidAccountTypeException("BankAccount type must be from type SavingAccount");
        }

        SavingAccount savingAccount = (SavingAccount)account;

        double maximum_withdraw = savingAccount.getBalance() - savingAccount.getMinimumBalance();
        if (amount > maximum_withdraw) {
            throw new InvalidTransactionException("Can not withdraw more than " + maximum_withdraw);
        }

        super.withdraw(savingAccount, amount);
    }
}
