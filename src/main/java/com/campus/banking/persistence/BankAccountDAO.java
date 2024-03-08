package com.campus.banking.persistence;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.campus.banking.model.BankAccount;

public interface BankAccountDAO<T extends BankAccount> {

    void add(T account);

    void transactionalAdd(Connection trxConn, T account);

    Optional<T> findByAccountNumber(String accountNumber);

    Optional<T> findByAccountNumberForUpdate(Connection trxConn, String accountNumber);

    void update(T account);

    void transactionalUpdate(Connection trxConn, T account);

    void inTransaction(Consumer<Connection> consumer);

    List<T> list();

    double sumBalanceHigherThan(double min);
}