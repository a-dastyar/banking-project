package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;

import com.campus.banking.exception.InvalidAccountException;
import com.campus.banking.model.BankAccount;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractBankAccountDAOImpl<T extends BankAccount> implements BankAccountDAO<T> {

    private final Database db;

    @Override
    public void add(T account) {
        validate(account);
        db.add(account);
    }

    private void validate(T account) {
        if (account == null || account.getAccountNumber() == null || account.getAccountNumber().isEmpty()) {
            throw new InvalidAccountException();
        }
    }

    @Override
    public Optional<T> findByAccountNumber(String accountNumber) {
        validateAccountNumber(accountNumber);
        return Optional.ofNullable(db.get(accountNumber,getType()));
    }

    private void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Account number can not be null or blank");
        }
    }

    @Override
    public void removeByAccountNumber(String accountNumber) {
        validateAccountNumber(accountNumber);
        db.remove(accountNumber);
    }

    @Override
    public List<T> list() {
        return db.list(getType());
    }

    abstract Class<T> getType();
}