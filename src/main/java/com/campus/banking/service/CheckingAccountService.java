package com.campus.banking.service;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.InvalidAccountTypeException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;

public class CheckingAccountService extends BankAccountServiceImpl {
    @Override
    public void deposit(BankAccount account, double amount) {
        super.deposit(account, amount);
    }

    @Override
    public void withdraw(BankAccount account, double amount) {
        if (!(account instanceof CheckingAccount)) {
            throw new InvalidAccountTypeException("BankAccount type must be from type CheckingAccount");
        }

        CheckingAccount checkingAccount = (CheckingAccount)account;

        if (checkingAccount.getDebt() > 0.0d) {
            throw new InsufficientFundsException("Can not withdraw while you have debt");
        }

        double balance = checkingAccount.getBalance();
        if (amount > balance &&
            amount <= balance + checkingAccount.getOverDraftLimit()) {
            checkingAccount.setDebt(amount - balance);
            amount = balance;
        }

        super.withdraw(checkingAccount, amount);
    }
}
