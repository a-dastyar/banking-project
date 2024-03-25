package com.campus.banking.service;

import java.time.LocalDateTime;
import java.util.List;

import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.Order;
import com.campus.banking.persistence.Page;
import com.campus.banking.persistence.TransactionDAO;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractAccountServiceImpl<T extends BankAccount> implements BankAccountService<T>{

    private BankAccountDAO<T> dao;

    protected TransactionDAO trxDao;

    protected int maxPageSize;


    protected String getUsername(T account) {
        if (account.getAccountHolder() == null
                || account.getAccountHolder().getUsername() == null
                || account.getAccountHolder().getUsername().isBlank()) {
            throw new IllegalArgumentException();
        }
        return account.getAccountHolder().getUsername();
    }

    @Override
    public double getAllowedWithdraw(@NotNull @Valid T account) {
        return account.getBalance();
    }
    
    @Override
    public T getByAccountNumber(@NotNull @NotBlank String accountNumber) {
        return dao.findByAccountNumber(accountNumber)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<T> getByUsername(@NotNull @NotBlank String username) {
        return dao.findByUsername(username);
    }

    @Override
    public Page<T> getPage(@Positive int page) {
        return dao.getAll(page, maxPageSize);
    }

    @Override
    public double getMinimumDeposit(@NotNull @Valid T account) {
        return 10;
    }

    @Override
    public double sumBalanceHigherThan(@PositiveOrZero double min) {
        return dao.sumBalanceHigherThan(min);
    }

    @Override
    public Page<Transaction> getTransactions(@NotNull @NotBlank String accountNumber, @Positive int page) {
        var found = getByAccountNumber(accountNumber);
        return trxDao.findByOrdered("account", found, page, maxPageSize, "date", Order.DESC);
    }

    protected void insertTransaction(EntityManager em, T account, double amount, TransactionType type) {
        var trx = Transaction.builder()
                .account(account)
                .amount(amount)
                .date(LocalDateTime.now())
                .type(type).build();
        trxDao.transactionalPersist(em, trx);
    }

}
