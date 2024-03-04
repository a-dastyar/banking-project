package com.campus.banking.persistence;

import java.util.List;

import com.campus.banking.exception.LoadFailureException;
import com.campus.banking.exception.SaveFailureException;
import com.campus.banking.model.BankAccount;

public interface Database {

    <T extends BankAccount> void add(T account);

    <T extends BankAccount> T get(String accountNumber,Class<T> clazz);

    void remove(String accountNumber);

    <T extends BankAccount> List<T> list(Class<T> clazz);

    void persist() throws SaveFailureException;

    void load() throws LoadFailureException;

    void clear() throws SaveFailureException;
}