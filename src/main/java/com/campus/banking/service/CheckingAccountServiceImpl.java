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

        withdraw(checkingAccount, 0);

        super.deposit(checkingAccount, amount);
    }

    @Override
    public void withdraw(BankAccount account, double amount) {
        if (!(account instanceof CheckingAccount)) {
            throw new InvalidAccountTypeException("BankAccount type must be from type CheckingAccount");
        }

        CheckingAccount checkingAccount = (CheckingAccount)account;

        if (checkingAccount.getDebt() == checkingAccount.getOverDraftLimit()) {
            throw new InsufficientFundsException("Can not withdraw while you have maximum debt");
        }

        if (amount + CheckingAccount.TRANSACTION_FEE + checkingAccount.getDebt() > account.getBalance() + checkingAccount.getOverDraftLimit()) {
            throw new InsufficientFundsException("Can not withdraw more than your balance: " + checkingAccount.getBalance() + " + your over draft limit: " + checkingAccount.getOverDraftLimit());
        }

        amount = getOverDraft(checkingAccount, amount);

        if (checkingAccount.getBalance() != 0.0d) {
            super.withdraw(checkingAccount, amount);
            super.withdraw(checkingAccount, CheckingAccount.TRANSACTION_FEE);
        }
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
        double debt = checkingAccount.getDebt();
        int fee = CheckingAccount.TRANSACTION_FEE;

        if (amount + fee > balance &&
            amount + fee <= balance + checkingAccount.getOverDraftLimit() - debt) {
                checkingAccount.setDebt(debt + amount + fee - balance);
                amount = balance - fee;
        }

        return amount;
    }
}
