package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;

import com.campus.banking.model.BankAccount;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BankAccountDAOImpl implements BankAccountDAO {

    private final Database db;

    @Override
    public void add(BankAccount account) {
        db.add(account);
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return Optional.ofNullable(db.get(accountNumber));
    }

    @Override
    public void removeByAccountNumber(String accountNumber) {
        db.remove(accountNumber);
    }

    @Override
    public List<BankAccount> list() {
        return db.list();
    }

}
