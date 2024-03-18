package com.campus.banking.service;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.LessThanMinimumTransactionException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.Page;
import com.campus.banking.persistence.TransactionDAO;

import java.time.LocalDateTime;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@ApplicationScoped
class CheckingAccountServiceImpl implements CheckingAccountService {

    private BankAccountDAO<CheckingAccount> dao;
    private TransactionDAO trxDao;
    private UserService users;
    private int maxPageSize;

    @Inject
    public CheckingAccountServiceImpl(BankAccountDAO<CheckingAccount> dao, TransactionDAO trxDao, UserService users,
            @ConfigProperty(name = "app.pagination.max_size") int maxPageSize) {
        this.dao = dao;
        this.trxDao = trxDao;
        this.users = users;
        this.maxPageSize = maxPageSize;
    }

    @Override
    public void add(@NotNull @Valid CheckingAccount account) {
        var user = users.getByUsername(getUsername(account));
        account.setAccountHolder(user);
        dao.inTransaction(em -> {
            var amount = account.getBalance();
            if (amount <= CheckingAccount.TRANSACTION_FEE)
                throw new LessThanMinimumTransactionException();

            account.setBalance(amount - CheckingAccount.TRANSACTION_FEE);
            dao.transactionalPersist(em, account);
            insertTransaction(em, account, amount, TransactionType.DEPOSIT);
            insertTransaction(em, account, CheckingAccount.TRANSACTION_FEE, TransactionType.TRANSACTION_FEE);
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
    public List<CheckingAccount> getByUsername(@NotNull @NotBlank String username) {
        return dao.findByUsername(username);
    }
    
    @Override
    public Page<CheckingAccount> getPage(@Positive int page) {
        return dao.getAll(page, maxPageSize);
    }

    @Override
    public CheckingAccount getByAccountNumber(@NotNull @NotBlank String accountNumber) {
        return dao.findByAccountNumber(accountNumber)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public void deposit(@NotNull @NotBlank String accountNumber, @Positive double amount) {
        if (amount <= CheckingAccount.TRANSACTION_FEE)
            throw new LessThanMinimumTransactionException();

        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(NotFoundException::new);
            doWithdraw(em, account, CheckingAccount.TRANSACTION_FEE);
            insertTransaction(em, account, CheckingAccount.TRANSACTION_FEE, TransactionType.TRANSACTION_FEE);
            doDeposit(em, account, amount);
            insertTransaction(em, account, amount, TransactionType.DEPOSIT);
        });
    }

    private void doDeposit(EntityManager em, CheckingAccount account, double amount) {
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
        dao.transactionalUpdate(em, account);
    }

    @Override
    public void withdraw(@NotNull @NotBlank String accountNumber, @Positive double amount) {
        if (amount <= CheckingAccount.TRANSACTION_FEE)
            throw new LessThanMinimumTransactionException();

        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(NotFoundException::new);

            var allowedWithdrawAmount = getAllowedWithdrawAmount(account);

            if (amount + CheckingAccount.TRANSACTION_FEE > allowedWithdrawAmount) {
                throw new InsufficientFundsException();
            }

            doWithdraw(em, account, CheckingAccount.TRANSACTION_FEE);
            insertTransaction(em, account, CheckingAccount.TRANSACTION_FEE, TransactionType.TRANSACTION_FEE);
            doWithdraw(em, account, amount);
            insertTransaction(em, account, amount, TransactionType.WITHDRAW);
        });
    }

    private void doWithdraw(EntityManager em, CheckingAccount account, double amount) {
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
        dao.transactionalUpdate(em, account);
    }

    private double getAllowedWithdrawAmount(CheckingAccount checkingAccount) {
        var amount = checkingAccount.getBalance();

        // Add overdraft
        amount += (checkingAccount.getOverdraftLimit() - checkingAccount.getDebt());

        // Don't allow to empty the account so that there is enough amount for
        // deposit transaction fee
        amount -= CheckingAccount.TRANSACTION_FEE;
        return amount;
    }

    @Override
    public double sumBalanceHigherThan(@PositiveOrZero double min) {
        return dao.sumBalanceHigherThan(min);
    }

    private void insertTransaction(EntityManager em, CheckingAccount account, double amount, TransactionType type) {
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
