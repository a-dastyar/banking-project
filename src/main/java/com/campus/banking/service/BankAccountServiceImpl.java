package com.campus.banking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.Page;
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

    private BankAccountDAO<BankAccount> dao;
    private TransactionDAO trxDao;
    private UserService users;
    private int maxPageSize;

    @Inject
    public BankAccountServiceImpl(BankAccountDAO<BankAccount> dao, TransactionDAO trxDao, UserService users,
            @ConfigProperty(name = "app.pagination.max_size") int maxPageSize) {
        this.dao = dao;
        this.trxDao = trxDao;
        this.users = users;
        this.maxPageSize = maxPageSize;
    }

    @Override
    public void add(@NotNull @Valid BankAccount account) {
        var user = users.getByUsername(getUsername(account));
        account.setAccountHolder(user);
        dao.inTransaction(em -> {
            account.setId(null);
            dao.transactionalPersist(em, account);
            if (account.getBalance() > 0.0d) {
                insertTransaction(em, account, account.getBalance(), TransactionType.DEPOSIT);
            }
        });
    }

    private String getUsername(BankAccount account) {
        if (account.getAccountHolder() == null
                || account.getAccountHolder().getUsername() == null
                || account.getAccountHolder().getUsername().isBlank()) {
            throw new IllegalArgumentException();
        }
        return account.getAccountHolder().getUsername();
    }

    @Override
    public BankAccount getByAccountNumber(@NotNull @NotBlank String accountNumber) {
        return dao.findByAccountNumber(accountNumber)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<BankAccount> getByUsername(@NotNull @NotBlank String username) {
        return dao.findByUsername(username);
    }

    @Override
    public Page<BankAccount> getPage(@Positive int page) {
        return dao.getAll(page, maxPageSize);
    }

    @Override
    public void deposit(@NotNull @NotBlank String accountNumber, @Positive double amount) {
        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(NotFoundException::new);
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
                    .orElseThrow(NotFoundException::new);
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
    public double getAllowedWithdraw(@NotNull @Valid BankAccount account) {
        return account.getBalance();
    }

    @Override
    public double getMinimumDeposit(@NotNull @Valid BankAccount account) {
        return 10;
    }

    @Override
    public double sumBalanceHigherThan(@PositiveOrZero double min) {
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

    @Override
    public Page<Transaction> getTransactions(@NotNull @NotBlank String accountNumber, @Positive int page) {
        var found = getByAccountNumber(accountNumber);
        return trxDao.findBy("account", found, page, maxPageSize);
    }
}
