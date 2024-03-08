package com.campus.banking.service;

import java.sql.Connection;
import java.time.LocalDateTime;

import com.campus.banking.exception.InvalidAccountException;
import com.campus.banking.exception.InvalidTransactionException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.SavingAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SavingAccountServiceImpl implements SavingAccountService {

    private SavingAccountDAO dao;
    private TransactionDAO trxDao;


    @Override
    public void add(SavingAccount account) {
        validate(account);
        dao.add(account);
    }

    @Override
    public SavingAccount getByAccountNumber(String accountNumber) {
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

    private void validate(SavingAccount account) {
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
    public void withdraw(String accountNumber, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not withdraw negative amount");
        }
        dao.inTransaction(conn -> {
            var account = dao.findByAccountNumberForUpdate(conn, accountNumber)
                    .orElseThrow(() -> new NotFoundException());
            doWithdraw(amount, conn, account);
            insertTransaction(conn, account, amount, TransactionType.INTEREST);
        });
    }

    private void doWithdraw(double amount, Connection conn, SavingAccount account) {
        double maximum_withdraw = account.getBalance() - account.getMinimumBalance();
        if (amount > maximum_withdraw) {
            throw new InvalidTransactionException("Can not withdraw more than " + maximum_withdraw);
        }
        account.setBalance(account.getBalance() - amount);
        dao.transactionalUpdate(conn, account);
    }

    @Override
    public void applyInterest(String accountNumber) {
        dao.inTransaction(conn -> {
            var account = dao.findByAccountNumberForUpdate(conn, accountNumber)
                    .orElseThrow(() -> new NotFoundException());

            double interest = account.getBalance() * account.getInterestRate() / 100.0;
            doDeposit(conn, account, interest);
            insertTransaction(conn, account, interest, TransactionType.INTEREST);
        });
    }

    @Override
    public void deposit(String accountNumber, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not withdraw negative amount");
        }
        dao.inTransaction(conn -> {
            var account = dao.findByAccountNumberForUpdate(conn, accountNumber)
                    .orElseThrow(() -> new NotFoundException());
            doDeposit(conn, account, amount);
            insertTransaction(conn, account, amount, TransactionType.DEPOSIT);
        });
    }

    private void doDeposit(Connection conn, SavingAccount account, double amount) {
        account.setBalance(account.getBalance() + amount);
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
    public void applyInterest() {
        dao.applyInterest();
    }

}
