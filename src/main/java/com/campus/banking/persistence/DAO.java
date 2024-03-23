package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.campus.banking.model.BaseModel;

import jakarta.persistence.EntityManager;

public interface DAO<T extends BaseModel<S>, S> {

    Optional<T> find(S id);

    void persist(T entity);

    void transactionalPersist(EntityManager em, T entity);

    void update(T entity);

    void transactionalUpdate(EntityManager em, T entity);

    void transactionalRemove(EntityManager em, T entity);

    void persist(List<T> entity);

    void update(List<T> entity);

    List<T> getAll();

    Page<T> getAll(int page, int size);

    long countAll();

    <U> long countBy(String fieldName, U fieldValue);

    <U> Page<T> findBy(String fieldName, U fieldValue, int page, int size);

    <U> List<T> findBy(String fieldName, U fieldValue);

    <U> List<T> findByForUpdate(EntityManager em, String fieldName, U fieldValue);

    <U> int removeBy(String fieldName, U fieldValue);

    boolean exists(T entity);

    <U> U withEntityManager(Function<EntityManager, U> action);

    void inTransaction(Consumer<EntityManager> action);

    <U> U inTransactionReturn(Function<EntityManager, U> action);
}