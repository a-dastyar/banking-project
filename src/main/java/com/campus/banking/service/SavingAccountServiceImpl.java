package com.campus.banking.service;

import java.time.LocalDateTime;

import com.campus.banking.exception.InvalidAccountException;
import com.campus.banking.exception.InvalidTransactionException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.SavingAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
class SavingAccountServiceImpl implements SavingAccountService {

    private SavingAccountDAO dao;
    private TransactionDAO trxDao;

    @Inject
    public SavingAccountServiceImpl(SavingAccountDAO dao, TransactionDAO trxDao) {
        this.dao = dao;
        this.trxDao = trxDao;
    }

    @Override
    public void add(SavingAccount account) {
        validate(account);
        dao.persist(account);
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
    public void deposit(String accountNumber, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not withdraw negative amount");
        }
        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(() -> new NotFoundException());
            doDeposit(em, account, amount);
            insertTransaction(em, account, amount, TransactionType.DEPOSIT);
        });
    }

    private void doDeposit(EntityManager em, SavingAccount account, double amount) {
        account.setBalance(account.getBalance() + amount);
        dao.transactionalUpdate(em, account);
    }

    @Override
    public void withdraw(String accountNumber, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not withdraw negative amount");
        }
        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(() -> new NotFoundException());
            doWithdraw(em, account, amount);
            insertTransaction(em, account, amount, TransactionType.WITHDRAW);
        });

    }

    private void doWithdraw(EntityManager em, SavingAccount account, double amount) {
        double maximum_withdraw = account.getBalance() - account.getMinimumBalance();
        if (amount > maximum_withdraw) {
            throw new InvalidTransactionException("Can not withdraw more than " + maximum_withdraw);
        }
        account.setBalance(account.getBalance() - amount);
        dao.transactionalUpdate(em, account);
    }

    @Override
    public void applyInterest(String accountNumber) {
        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(() -> new NotFoundException());

            var interest = account.getBalance() * account.getInterestRate() / 100.0;
            account.setBalance(account.getBalance() + interest);

            dao.transactionalUpdate(em, account);
            insertTransaction(em, account, interest, TransactionType.INTEREST);
        });

    }

    @Override
    public void applyInterest() {
        dao.applyInterest();
    }

    @Override
    public double sumBalanceHigherThan(double min) {
        return dao.sumBalanceHigherThan(min);
    }

    private void insertTransaction(EntityManager em, SavingAccount account, double amount, TransactionType type) {
        var trx = Transaction.builder()
                .account(account)
                .amount(amount)
                .date(LocalDateTime.now())
                .type(type).build();
        trxDao.transactionalPersist(em, trx);
    }
}
