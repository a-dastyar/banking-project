package com.campus.banking.service;

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

        SavingAccount savingAccount = (SavingAccount)account;

        double maximum_withdraw = savingAccount.getBalance() - savingAccount.getMinimumBalance();
        if (amount > maximum_withdraw) {
            throw new InvalidTransactionException("Can not withdraw more than " + maximum_withdraw);
        }

        super.withdraw(savingAccount, amount);
    }

    @Override
    public void applyInterest(SavingAccount account) {
        double interest = account.getBalance() * account.getInterestRate();

        deposit(account, interest);
    }
}
