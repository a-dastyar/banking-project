package com.campus.banking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.campus.banking.exception.InvalidTransactionException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.Page;
import com.campus.banking.persistence.SavingAccountDAO;
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
class SavingAccountServiceImpl implements SavingAccountService {

    private SavingAccountDAO dao;
    private TransactionDAO trxDao;
    private UserService users;
    private int maxPageSize;

    @Inject
    public SavingAccountServiceImpl(SavingAccountDAO dao, TransactionDAO trxDao, UserService users,
            @ConfigProperty(name = "app.pagination.max_size") int maxPageSize) {
        this.dao = dao;
        this.trxDao = trxDao;
        this.users = users;
        this.maxPageSize = maxPageSize;
    }

    @Override
    public void add(@NotNull @Valid SavingAccount account) {
        var user = users.getByUsername(getUsername(account));
        dao.inTransaction(em -> {
            account.setAccountHolder(user);
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
    public SavingAccount getByAccountNumber(@NotNull @NotBlank String accountNumber) {
        return dao.findByAccountNumber(accountNumber)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<SavingAccount> getByUsername(@NotNull @NotBlank String username) {
        return dao.findByUsername(username);
    }

    @Override
    public Page<SavingAccount> getPage(@Positive int page) {
        return dao.getAll(page, maxPageSize);
    }

    @Override
    public void deposit(@NotNull @NotBlank String accountNumber, @Positive double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not withdraw negative amount");
        }
        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(NotFoundException::new);
            doDeposit(em, account, amount);
            insertTransaction(em, account, amount, TransactionType.DEPOSIT);
        });
    }

    private void doDeposit(EntityManager em, SavingAccount account, @Positive double amount) {
        account.setBalance(account.getBalance() + amount);
        dao.transactionalUpdate(em, account);
    }

    @Override
    public void withdraw(@NotNull @NotBlank String accountNumber, @Positive double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can not withdraw negative amount");
        }
        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(NotFoundException::new);
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
    public void applyInterest(@NotNull @NotBlank String accountNumber) {
        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(NotFoundException::new);

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
    public double sumBalanceHigherThan(@PositiveOrZero double min) {
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

    @Override
    public Page<Transaction> getTransactions(@NotNull @NotBlank String accountNumber, @Positive int page) {
        var found = getByAccountNumber(accountNumber);
        return trxDao.findBy("account", found, page, maxPageSize);
    }
}
