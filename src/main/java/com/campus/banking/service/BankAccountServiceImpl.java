package com.campus.banking.service;

import java.time.LocalDateTime;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.InvalidAccountException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
class BankAccountServiceImpl implements BankAccountService<BankAccount> {

    protected BankAccountDAO<BankAccount> dao;
    protected TransactionDAO trxDao;

    @Inject
    public BankAccountServiceImpl(BankAccountDAO<BankAccount> dao, TransactionDAO trxDao) {
        this.dao = dao;
        this.trxDao = trxDao;
    }

    @Override
    public void add(BankAccount account) {
        validate(account);
        dao.persist(account);
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
        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(() -> new NotFoundException());
            doDeposit(em, account, amount);
            insertTransaction(em, account, amount, TransactionType.DEPOSIT);
        });
    }

    private void doDeposit(EntityManager em, BankAccount account, double amount) {
        account.setBalance(account.getBalance() + amount);
        dao.transactionalPersist(em, account);
    }

    @Override
    public void withdraw(String accountNumber, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not withdraw negative amount");
        }
        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(() -> new NotFoundException());
            if (amount > account.getBalance()) {
                throw new InsufficientFundsException();
            }
            doWithdraw(em, account, amount);
            insertTransaction(em, account, amount, TransactionType.WITHDRAW);
        });
    }

    private void doWithdraw(EntityManager em, BankAccount account, double amount) {
        account.setBalance(account.getBalance() - amount);
        dao.transactionalUpdate(em, account);
    }

    @Override
    public double sumBalanceHigherThan(double min) {
        return dao.sumBalanceHigherThan(min);
    }

    private void insertTransaction(EntityManager em, BankAccount account, double amount, TransactionType type) {
        var trx = Transaction.builder()
                .account(account)
                .amount(amount)
                .date(LocalDateTime.now())
                .type(type).build();
        trxDao.transactionalPersist(em, trx);
    }
}
