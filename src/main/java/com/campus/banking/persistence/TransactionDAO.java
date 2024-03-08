package com.campus.banking.persistence;

import java.sql.Connection;

import com.campus.banking.model.Transaction;

public interface TransactionDAO {
    void addTransaction(Connection conn,Transaction transaction);
}
