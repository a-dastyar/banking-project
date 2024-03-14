package com.campus.banking.service;

import java.time.LocalDateTime;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

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
    public void add(@NotNull @Valid BankAccount account) {
        dao.persist(account);
    }

    @Override
    public @NotNull @Valid BankAccount getByAccountNumber(@NotNull @NotBlank String accountNumber) {
        return dao.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException());
    }

    @Override
    public void deposit(@NotNull @NotBlank String accountNumber, @Positive double amount) {
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
    public void withdraw(@NotNull @NotBlank String accountNumber, @Positive double amount) {
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
    public @PositiveOrZero double sumBalanceHigherThan(@PositiveOrZero double min) {
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
