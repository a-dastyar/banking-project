package com.campus.banking.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.campus.banking.exception.InvalidArgumentException;
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
public abstract class AbstractAccountServiceImpl<T extends BankAccount> implements BankAccountService<T> {

    private final BankAccountDAO<T> dao;

    protected final TransactionDAO trxDao;

    protected final int maxPageSize;

    protected final int defaultPageSize;

    protected String getUsername(T account) {
        if (account.getAccountHolder() == null
                || account.getAccountHolder().getUsername() == null
                || account.getAccountHolder().getUsername().isBlank()) {
            throw InvalidArgumentException.BLANK_USERNAME;
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
                .orElseThrow(() -> NotFoundException.ACCOUNT_NOT_FOUND);
    }

    @Override
    public Page<T> getByUsername(@NotNull @NotBlank String username, @Positive int page, Optional<Integer> size) {
        var pageSize = size.filter(i -> i <= maxPageSize).orElse(defaultPageSize);
        return dao.findByUsername(username, page, pageSize);
    }

    @Override
    public Page<T> getPage(@Positive int page, Optional<Integer> size) {
        var pageSize = size.filter(i -> i <= maxPageSize).orElse(defaultPageSize);
        return dao.getAll(page, pageSize);
    }

    @Override
    public double getMinimumWithdraw(@NotNull @Valid T account) {
        return 10;
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
    public Page<Transaction> getTransactions(@NotNull @NotBlank String accountNumber, @Positive int page, Optional<Integer> size) {
        var pageSize = size.filter(i -> i <= maxPageSize).orElse(defaultPageSize);
        var found = getByAccountNumber(accountNumber);
        return trxDao.findByOrdered("account", found, page, pageSize, "date", Order.DESC);
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
