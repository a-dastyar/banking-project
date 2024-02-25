package com.campus.banking.service;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.InvalidAccountTypeException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;

public class CheckingAccountServiceImpl extends BankAccountServiceImpl implements CheckingAccountService {
    @Override
    public void deposit(BankAccount account, double amount) {
        if (!(account instanceof CheckingAccount)) {
            throw new InvalidAccountTypeException("BankAccount type must be from type CheckingAccount");
        }

        CheckingAccount checkingAccount = (CheckingAccount)account;

        amount = payDebt(checkingAccount, amount);

        super.deposit(checkingAccount, amount);
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

        amount = getOverDraft(checkingAccount, amount);

        super.withdraw(checkingAccount, amount);
    }

    private double payDebt(CheckingAccount checkingAccount, double amount) {
        double debt = checkingAccount.getDebt();
        if (debt > 0.0d && amount > 0.0d) {
            if (amount >= debt) {
                checkingAccount.setDebt(0.0d);
                amount -= debt;
            } else {
                checkingAccount.setDebt(debt - amount);
                amount = 0.0d;
            }
        }

        return amount;
    }

    private double getOverDraft(CheckingAccount checkingAccount, double amount) {
        double balance = checkingAccount.getBalance();
        if (amount > balance &&
            amount <= balance + checkingAccount.getOverDraftLimit()) {
                checkingAccount.setDebt(amount - balance);
                amount = balance;
        }

        return amount;
    }
}
