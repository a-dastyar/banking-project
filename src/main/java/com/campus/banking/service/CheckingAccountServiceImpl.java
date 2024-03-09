package com.campus.banking.service;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.InvalidAccountException;
import com.campus.banking.exception.LessThanMinimumTransactionException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.TransactionType;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CheckingAccountServiceImpl implements CheckingAccountService {

    protected BankAccountDAO<CheckingAccount> dao;
    protected TransactionDAO trxDao;

    @Override
    public void add(CheckingAccount account) {
        validate(account);
        dao.persist(account);
    }

    private void validate(CheckingAccount account) {
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
    public CheckingAccount getByAccountNumber(String accountNumber) {
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
        if (amount < 0)
            throw new IllegalArgumentException("Can not deposit negative amount");

        if (amount <= CheckingAccount.TRANSACTION_FEE)
            throw new LessThanMinimumTransactionException();

        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(() -> new NotFoundException());
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
    public void withdraw(String accountNumber, double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Can not withdraw negative amount");

        if (amount <= CheckingAccount.TRANSACTION_FEE)
            throw new LessThanMinimumTransactionException();

        dao.inTransaction(em -> {
            var account = dao.findByAccountNumberForUpdate(em, accountNumber)
                    .orElseThrow(() -> new NotFoundException());

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
        amount += (checkingAccount.getOverDraftLimit() - checkingAccount.getDebt());

        // Don't allow to empty the account so that there is enough amount for
        // deposit transaction fee
        amount -= CheckingAccount.TRANSACTION_FEE;
        return amount;
    }

    @Override
    public double sumBalanceHigherThan(double min) {
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
}
