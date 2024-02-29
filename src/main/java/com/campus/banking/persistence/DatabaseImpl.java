package com.campus.banking.persistence;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.campus.banking.exception.LoadFailureException;
import com.campus.banking.exception.SaveFailureException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.utils.AutoCloseableLock;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public enum DatabaseImpl implements Database {
    INSTANCE;

    private Map<String, BankAccount> map = new HashMap<>();
    private ObjectMapper mapper = new ObjectMapper();
    private AutoCloseableLock lock = new AutoCloseableLock(new ReentrantLock());
    private String filePath = "database.json";

    @Override
    public <T extends BankAccount> void add(T account) {
        if (account == null || account.getAccountNumber() == null || account.getAccountNumber().isBlank()) {
            throw new IllegalArgumentException("Account and account number can not be null or blank");
        }
        try (var l = lock.lock()) {
            map.put(account.getAccountNumber(), account);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BankAccount> T get(String accountNumber, Class<T> clazz) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Account number can not be null or blank");
        }
        var account = map.get(accountNumber);
        // TODO check for subclass
        if (clazz.isInstance(account)) {
            return (T) account;
        }
        return null;
    }

    @Override
    public void remove(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Account number can not be null or blank");
        }
        try (var l = lock.lock()) {
            map.remove(accountNumber);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BankAccount> List<T> list(Class<T> clazz) {
        try (var l = lock.lock()) {
            return (List<T>) map.values().stream()
                    .filter(account -> clazz.isInstance(account))
                    .toList();
        }
    }

    @Override
    public void persist() throws SaveFailureException {
        try (var l = lock.lock()) {
            mapper.writerFor(new TypeReference<Map<String, BankAccount>>() {
            })
                    .withDefaultPrettyPrinter()
                    .writeValue(new File(this.filePath), map);
        } catch (IOException e) {
            throw new SaveFailureException(e);
        }
    }

    @Override
    public void load() throws LoadFailureException {
        try (var l = lock.lock()) {
            map = mapper.readValue(new File(this.filePath), new TypeReference<Map<String, BankAccount>>() {
            });
        } catch (IOException e) {
            throw new LoadFailureException(e);
        }
    }

    @Override
    public void clear() throws SaveFailureException {
        try (var l = lock.lock()) {
            map = new HashMap<>();
            persist();
        }
    }

}