package com.campus.banking.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.AccountType;
import com.campus.banking.model.BankAccount;
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

@ApplicationScoped
class BankAccountServiceImpl extends AbstractAccountServiceImpl<BankAccount> {

    private final BankAccountDAO<BankAccount> dao;

    private final AccountNumberGenerator generator;

    private final UserService users;

    @Inject
    public BankAccountServiceImpl(BankAccountDAO<BankAccount> dao, TransactionDAO trxDao,
            AccountNumberGenerator generator, UserService users,
            @ConfigProperty(name = "app.pagination.max_size") int maxPageSize,
            @ConfigProperty(name = "app.pagination.default_size") int defaultPageSize) {
        super(dao, trxDao, maxPageSize,defaultPageSize);
        this.dao = dao;
        this.generator = generator;
        this.users = users;
    }

    @Override
    public void add(@NotNull @Valid BankAccount account) {
        var user = users.getByUsername(getUsername(account));
        account.setAccountHolder(user);
        dao.inTransaction(em -> {
            account.setId(null);
            account.setAccountNumber(generator.transactionalGenerate(em, AccountType.BANK));
            dao.transactionalPersist(em, account);
            if (account.getBalance() > 0.0d) {
                insertTransaction(em, account, account.getBalance(), TransactionType.DEPOSIT);
            }
        });
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

}
