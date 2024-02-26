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
        var debt = account.getDebt();
        var balance = account.getBalance();
        if (account.getDebt() > 0.0d) {
            if (account.getDebt() >= amount) {
                debt -= amount;
            } else {
                debt = 0;
                balance += amount - account.getDebt();
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
        var debt = account.getDebt();
        var balance = account.getBalance();
        if (amount > account.getBalance()) {
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

        // Don't allow to empty the account so that there is not enough amount for
        // deposit transaction fee
        amount -= CheckingAccount.TRANSACTION_FEE;
        return amount;
    }
}
