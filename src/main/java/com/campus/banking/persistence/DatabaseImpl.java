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
    public void add(BankAccount account) {
        try (var l = lock.lock()) {
            map.put(account.getAccountNumber(), account);
        }
    }

    @Override
    public BankAccount get(String accountNumber) {
        return map.get(accountNumber);
    }

    @Override
    public void remove(String accountNumber) {
        try (var l = lock.lock()) {
            map.remove(accountNumber);
        }
    }

    @Override
    public List<BankAccount> list() {
        try (var l = lock.lock()) {
            return map.values().stream().toList();
        }
    }

    @Override
    public void persist() throws SaveFailureException {
        try (var l = lock.lock()) {
            mapper.writerFor(new TypeReference<Map<String, BankAccount>>() {})
                    .withDefaultPrettyPrinter()
                    .writeValue(new File(this.filePath), map);
        } catch (IOException e) {
            throw new SaveFailureException(e);
        }
    }

    @Override
    public void load() throws LoadFailureException {
        try (var l = lock.lock()) {
            map =mapper.readValue(new File(this.filePath),new TypeReference<Map<String, BankAccount>>() {});
        } catch (IOException e) {
            throw new LoadFailureException(e);
        }
    }

}