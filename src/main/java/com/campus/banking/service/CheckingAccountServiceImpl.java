package com.campus.banking.service;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.InvalidAccountTypeException;
import com.campus.banking.exception.LessThanMinimumTransactionException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;

public class CheckingAccountServiceImpl extends BankAccountServiceImpl implements CheckingAccountService {
    
    @Override
    public void deposit(BankAccount account, double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Can not deposit negative amount");

        if (amount <= CheckingAccount.TRANSACTION_FEE) 
            throw new LessThanMinimumTransactionException();

        if (account instanceof CheckingAccount checkingAccount) {
            doWithdraw(checkingAccount, CheckingAccount.TRANSACTION_FEE);
            doDeposit(checkingAccount, amount);
        } else {
            throw new InvalidAccountTypeException("BankAccount type must be from type CheckingAccount");
        }
    }

    private void doDeposit(CheckingAccount account, double amount) {
        final var DEBT = account.getDebt();
        var debt = DEBT;
        var balance = account.getBalance();
        if (DEBT > 0.0d) {
            if (DEBT >= amount) {
                debt -= amount;
            } else {
                debt = 0;
                balance += amount - DEBT;
            }
        } else {
            balance += amount;
        }
        account.setDebt(debt);
        account.setBalance(balance);
    }

    @Override
    public void withdraw(BankAccount account, double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Can not withdraw negative amount");

        if (amount <= CheckingAccount.TRANSACTION_FEE) 
            throw new LessThanMinimumTransactionException();

        if (account instanceof CheckingAccount checkingAccount) {

            var allowedWithdrawAmount = getAllowedWithdrawAmount(checkingAccount);

            if (amount + CheckingAccount.TRANSACTION_FEE > allowedWithdrawAmount) {
                throw new InsufficientFundsException();
            }

            doWithdraw(checkingAccount, CheckingAccount.TRANSACTION_FEE);
            doWithdraw(checkingAccount, amount);

        } else {
            throw new InvalidAccountTypeException("BankAccount type must be from type CheckingAccount");
        }

    }

    private void doWithdraw(CheckingAccount account, double amount) {
        final var BALANCE = account.getBalance();
        var debt = account.getDebt();
        var balance = BALANCE;
        if (amount > BALANCE) {
            var overdraft = amount - balance;
            debt += overdraft;
            balance -= amount - overdraft;
        } else {
            balance -= amount;
        }
        account.setDebt(debt);
        account.setBalance(balance);
    }

    private double getAllowedWithdrawAmount(CheckingAccount checkingAccount) {
        var amount = checkingAccount.getBalance();

        // Add overdraft
        amount += (checkingAccount.getOverDraftLimit() - checkingAccount.getDebt());

        // Don't allow to empty the account so that there is enough amount for
        // deposit transaction fee
        amount -= CheckingAccount.TRANSACTION_FEE;
        return amount;
    }
}
