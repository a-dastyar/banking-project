package com.campus.banking.persistence;

import java.util.function.Consumer;
import com.campus.banking.model.Transaction;

import jakarta.persistence.EntityManager;

public interface TransactionDAO{

    void transactionalPersist(EntityManager em, Transaction entity);

    <U> Page<Transaction> findByOrdered(String fieldName, U fieldValue, int page, int size, String orderField, Order order);

    <U> long countBy(String fieldName, U fieldValue);

    void inTransaction(Consumer<EntityManager> action);
}
