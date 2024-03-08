package com.campus.banking.service;

import java.sql.Connection;
import java.time.LocalDateTime;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.InvalidAccountException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService<BankAccount> {

    protected BankAccountDAO<BankAccount> dao;
    protected TransactionDAO trxDao;

    @Override
    public void add(BankAccount account) {
        validate(account);
        dao.add(account);
    }

    private void validate(BankAccount account) {
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
    public BankAccount getByAccountNumber(String accountNumber) {
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
    public void deposit(String accountNumber, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not deposit negative amount");
        }
        dao.inTransaction(conn -> {
            var account = dao.findByAccountNumberForUpdate(conn, accountNumber)
                    .orElseThrow(() -> new NotFoundException());
            doDeposit(conn, account, amount);
            insertTransaction(conn, account, amount, TransactionType.DEPOSIT);
        });
    }

    private void doDeposit(Connection conn, BankAccount account, double amount) {
        account.setBalance(account.getBalance() + amount);
        dao.transactionalUpdate(conn, account);
    }

    @Override
    public void withdraw(String accountNumber, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not withdraw negative amount");
        }
        dao.inTransaction(conn -> {
            var account = dao.findByAccountNumberForUpdate(conn, accountNumber)
                    .orElseThrow(() -> new NotFoundException());
            doWithdraw(conn, account, amount);
            insertTransaction(conn, account, amount, TransactionType.WITHDRAW);
        });
    }

    private void doWithdraw(Connection conn, BankAccount account, double amount) {
        if (amount > account.getBalance()) {
            throw new InsufficientFundsException();
        }
        account.setBalance(account.getBalance() - amount);
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

    @Override
    public double sumBalanceHigherThan(double min) {
        return dao.sumBalanceHigherThan(min);
    }

}
