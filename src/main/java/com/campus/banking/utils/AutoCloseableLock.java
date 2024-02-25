package com.campus.banking.utils;

import java.util.concurrent.locks.Lock;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AutoCloseableLock implements AutoCloseable {
    private final Lock lock;

    public AutoCloseableLock lock() {
        lock.lock();
        return this;
    }

    public void unlock() {
        lock.unlock();
    }

    @Override
    public void close() {
        lock.unlock();
    }
}
