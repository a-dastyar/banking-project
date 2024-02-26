package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;

import com.campus.banking.exception.InvalidAccountException;
import com.campus.banking.model.BankAccount;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BankAccountDAOImpl implements BankAccountDAO {

    private final Database db;

    @Override
    public void add(BankAccount account) {
        validate(account);
        db.add(account);
    }

    private void validate(BankAccount account) {
        if (account == null || account.getAccountNumber() == null || account.getAccountNumber().isEmpty()) {
            throw new InvalidAccountException();
        }
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        validateAccountNumber(accountNumber);
        return Optional.ofNullable(db.get(accountNumber));
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
    public List<BankAccount> list() {
        return db.list();
    }

}
