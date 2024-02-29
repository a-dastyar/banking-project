package com.campus.banking.service;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.LessThanMinimumTransactionException;
import com.campus.banking.model.CheckingAccount;

public class CheckingAccountServiceImpl extends BankAccountServiceImpl<CheckingAccount>
        implements CheckingAccountService {

    @Override
    public void deposit(CheckingAccount account, double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Can not deposit negative amount");

        if (amount <= CheckingAccount.TRANSACTION_FEE)
            throw new LessThanMinimumTransactionException();

        doWithdraw(account, CheckingAccount.TRANSACTION_FEE);
        doDeposit(account, amount);
    }

    private void doDeposit(CheckingAccount account, double amount) {
        final var originalDebt = account.getDebt();
        var debt = originalDebt;
        var balance = account.getBalance();
        if (originalDebt > 0.0d) {
            if (originalDebt >= amount) {
                debt -= amount;
            } else {
                debt = 0;
                balance += amount - originalDebt;
            }
        } else {
            balance += amount;
        }
        account.setDebt(debt);
        account.setBalance(balance);
    }

    @Override
    public void withdraw(CheckingAccount account, double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Can not withdraw negative amount");

        if (amount <= CheckingAccount.TRANSACTION_FEE)
            throw new LessThanMinimumTransactionException();

        var allowedWithdrawAmount = getAllowedWithdrawAmount(account);

        if (amount + CheckingAccount.TRANSACTION_FEE > allowedWithdrawAmount) {
            throw new InsufficientFundsException();
        }

        doWithdraw(account, CheckingAccount.TRANSACTION_FEE);
        doWithdraw(account, amount);
    }

    private void doWithdraw(CheckingAccount account, double amount) {
        final var originalBalance = account.getBalance();
        var debt = account.getDebt();
        var balance = originalBalance;
        if (amount > originalBalance) {
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
