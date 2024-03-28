package com.campus.banking.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.campus.banking.exception.IllegalBalanceStateException;
import com.campus.banking.exception.InvalidTransactionException;
import com.campus.banking.exception.LessThanMinimumTransactionException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.AccountType;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.SavingAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@ApplicationScoped
class SavingAccountServiceImpl extends AbstractAccountServiceImpl<SavingAccount> implements SavingAccountService {

    private final SavingAccountDAO dao;

    private final AccountNumberGenerator generator;

    private final UserService users;

    @Inject
    public SavingAccountServiceImpl(SavingAccountDAO dao, TransactionDAO trxDao, AccountNumberGenerator generator,
            UserService users,
            @ConfigProperty(name = "app.pagination.max_size") int maxPageSize,
            @ConfigProperty(name = "app.pagination.default_size") int defaultPageSize) {
        super(dao, trxDao, maxPageSize, defaultPageSize);
        this.dao = dao;
        this.users = users;
        this.generator = generator;
    }

    @Override
    public void add(@NotNull @Valid SavingAccount account) {
        validateAccountInfo(account);
        var user = users.getByUsername(getUsername(account));
        dao.inTransaction(em -> {
            account.setAccountHolder(user);
            account.setId(null);
            account.setAccountNumber(generator.transactionalGenerate(em, AccountType.SAVING));
            dao.transactionalPersist(em, account);
            if (account.getBalance() > 0.0d) {
                insertTransaction(em, account, account.getBalance(), TransactionType.DEPOSIT);
            }
        });
    }

    private void validateAccountInfo(SavingAccount account) {
        if (account.getBalance() < account.getMinimumBalance()) {
            throw IllegalBalanceStateException.BALANCE_LESS_THAN_MINIMUM;
        }
    }

    @Override
    public void deposit(@NotNull @NotBlank String accountNumber, @Positive double amount) {
        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(() -> NotFoundException.ACCOUNT_NOT_FOUND);

            if (amount < getMinimumDeposit(account)) {
                throw LessThanMinimumTransactionException.EXCEPTION;
            }
            
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
        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(() -> NotFoundException.ACCOUNT_NOT_FOUND);
            doWithdraw(em, account, amount);
            insertTransaction(em, account, amount, TransactionType.WITHDRAW);
        });

    }

    private void doWithdraw(EntityManager em, SavingAccount account, double amount) {
        if (amount > getAllowedWithdraw(account)) {
            throw InvalidTransactionException.WITHDRAW_MORE_THAN_ALLOWED;
        }
        account.setBalance(account.getBalance() - amount);
        dao.transactionalUpdate(em, account);
    }

    @Override
    public double getAllowedWithdraw(@NotNull @Valid SavingAccount account) {
        return account.getBalance() - account.getMinimumBalance();
    }

    @Override
    public void applyInterest(@NotNull @NotBlank String accountNumber) {
        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(() -> NotFoundException.ACCOUNT_NOT_FOUND);

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
}
