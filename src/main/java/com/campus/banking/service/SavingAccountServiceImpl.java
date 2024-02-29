package com.campus.banking.service;

import com.campus.banking.exception.InvalidTransactionException;
import com.campus.banking.model.SavingAccount;

public class SavingAccountServiceImpl extends BankAccountServiceImpl<SavingAccount> implements SavingAccountService {

    @Override
    public void withdraw(SavingAccount account, double amount) {

        double maximum_withdraw = account.getBalance() - account.getMinimumBalance();
        if (amount > maximum_withdraw) {
            throw new InvalidTransactionException("Can not withdraw more than " + maximum_withdraw);
        }

        super.withdraw(account, amount);
    }

    @Override
    public void applyInterest(SavingAccount account) {
        double interest = account.getBalance() * SavingAccount.INTEREST_RATE;

        super.deposit(account, interest);
    }
}
