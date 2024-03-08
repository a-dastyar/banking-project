package com.campus.banking.service;

import java.sql.Connection;
import java.time.LocalDateTime;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.InvalidAccountException;
import com.campus.banking.exception.LessThanMinimumTransactionException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CheckingAccountServiceImpl implements CheckingAccountService {

    private BankAccountDAO<CheckingAccount> dao;
    private TransactionDAO trxDao;

    @Override
    public void add(CheckingAccount account) {
        validate(account);
        dao.add(account);
    }

    private void validate(CheckingAccount account) {
        if (account == null
                || account.getAccountNumber() == null
                || account.getAccountNumber().isBlank()
                || account.getAccountHolderName() == null
                || account.getAccountHolderName().isBlank()
                || account.getBalance() < 0) {
            throw new InvalidAccountException();
        }
    }

    @Override
    public CheckingAccount getByAccountNumber(String accountNumber) {
        validateAccountNumber(accountNumber);
        return dao.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException());
    }

    private void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public double sumBalanceHigherThan(double min) {
        return dao.sumBalanceHigherThan(min);
    }

    @Override
    public void deposit(String accountNumber, double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Can not deposit negative amount");

        if (amount <= CheckingAccount.TRANSACTION_FEE)
            throw new LessThanMinimumTransactionException();

        dao.inTransaction(conn -> {
            var account = dao.findByAccountNumberForUpdate(conn, accountNumber)
                    .orElseThrow(() -> new NotFoundException());

            doDeposit(conn, account, amount);
            insertTransaction(conn, account, amount, TransactionType.WITHDRAW);

            doWithdraw(conn, account, CheckingAccount.TRANSACTION_FEE);
            insertTransaction(conn, account, CheckingAccount.TRANSACTION_FEE, TransactionType.TRANSACTION_FEE);
        });
    }

    private void doDeposit(Connection conn, CheckingAccount account, double amount) {
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

        dao.transactionalUpdate(conn, account);
    }

    @Override
    public void withdraw(String accountNumber, double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Can not withdraw negative amount");

        if (amount <= CheckingAccount.TRANSACTION_FEE)
            throw new LessThanMinimumTransactionException();

        dao.inTransaction(conn -> {
            var account = dao.findByAccountNumberForUpdate(conn, accountNumber)
                    .orElseThrow(() -> new NotFoundException());

            var allowedWithdrawAmount = getAllowedWithdrawAmount(account);

            if (amount + CheckingAccount.TRANSACTION_FEE > allowedWithdrawAmount) {
                throw new InsufficientFundsException();
            }

            doWithdraw(conn, account, CheckingAccount.TRANSACTION_FEE);
            insertTransaction(conn, account, CheckingAccount.TRANSACTION_FEE, TransactionType.TRANSACTION_FEE);

            doWithdraw(conn, account, amount);
            insertTransaction(conn, account, amount, TransactionType.WITHDRAW);
        });
    }

    private void doWithdraw(Connection conn, CheckingAccount account, double amount) {
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

        dao.transactionalUpdate(conn, account);
    }

    private void insertTransaction(Connection conn, BankAccount account, double amount, TransactionType type) {
        var trx = Transaction.builder()
                .account(account)
                .amount(amount)
                .date(LocalDateTime.now())
                .type(type).build();
        trxDao.addTransaction(conn, trx);
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
