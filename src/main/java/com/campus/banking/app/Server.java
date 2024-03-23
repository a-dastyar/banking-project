package com.campus.banking.app;

public interface Server {
    void start() throws ServerFailureException;
    void stop() throws ServerFailureException;
} 
