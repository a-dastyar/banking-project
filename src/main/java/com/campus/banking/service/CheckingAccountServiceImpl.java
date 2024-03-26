package com.campus.banking.service;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.LessThanMinimumTransactionException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.TransactionDAO;
import com.campus.banking.service.AccountNumberGenerator.AccountType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
class CheckingAccountServiceImpl extends AbstractAccountServiceImpl<CheckingAccount> implements CheckingAccountService {

    private final BankAccountDAO<CheckingAccount> dao;

    private final AccountNumberGenerator generator;

    private final UserService users;

    @Inject
    public CheckingAccountServiceImpl(BankAccountDAO<CheckingAccount> dao, TransactionDAO trxDao,
            AccountNumberGenerator generator, UserService users,
            @ConfigProperty(name = "app.pagination.max_size") int maxPageSize) {
        super(dao, trxDao, maxPageSize);
        this.dao = dao;
        this.users = users;
        this.generator = generator;
    }

    @Override
    public void add(@NotNull @Valid CheckingAccount account) {
        validateAccountInfo(account);
        var user = users.getByUsername(getUsername(account));
        account.setAccountHolder(user);
        dao.inTransaction(em -> {
            var amount = account.getBalance();
            if (amount <= CheckingAccount.TRANSACTION_FEE)
                throw new LessThanMinimumTransactionException();

            account.setBalance(amount - CheckingAccount.TRANSACTION_FEE);
            account.setId(null);
            account.setAccountNumber(generator.transactionalGenerate(em, AccountType.CHECKING));
            dao.transactionalPersist(em, account);
            insertTransaction(em, account, amount, TransactionType.DEPOSIT);
            insertTransaction(em, account, CheckingAccount.TRANSACTION_FEE, TransactionType.TRANSACTION_FEE);
        });
    }

    private void validateAccountInfo(CheckingAccount account) {
        if (account.getBalance() > 0.0d && account.getDebt() > 0.0d)
            throw new IllegalArgumentException("Can't have balance while in debt");
        if (account.getDebt() > account.getOverdraftLimit())
            throw new IllegalArgumentException("Can't have debt more than overdraft limit");
    }

    @Override
    public void deposit(@NotNull @NotBlank String accountNumber, @Positive double amount) {
        log.debug("Deposit {} to Account[{}]", amount, accountNumber);
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

            var allowedWithdrawAmount = getAllowedWithdraw(account);

            if (amount > allowedWithdrawAmount) {
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

    @Override
    public double getAllowedWithdraw(@NotNull @Valid CheckingAccount account) {
        var amount = account.getBalance();

        // Add overdraft
        amount += (account.getOverdraftLimit() - account.getDebt());

        // Don't allow to empty the account so that there is enough amount for
        // deposit transaction fee
        amount -= CheckingAccount.TRANSACTION_FEE * 2;
        return amount < 0.0d ? 0.0d : amount;
    }

    @Override
    public double getMinimumDeposit(@NotNull @Valid CheckingAccount account) {
        return CheckingAccount.TRANSACTION_FEE * 2;
    }

}
