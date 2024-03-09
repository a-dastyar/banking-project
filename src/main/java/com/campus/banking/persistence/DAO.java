package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.campus.banking.model.BaseModel;

import jakarta.persistence.EntityManager;

public interface DAO<T extends BaseModel<S>, S> {

    Optional<T> find(S id);

    void persist(T entity);

    void transactionalPersist(EntityManager em, T entity);

    void update(T entity);

    void transactionalUpdate(EntityManager em, T entity);

    void remove(T entity);

    List<T> find(List<S> ids);

    void persist(List<T> entity);

    void update(List<T> entity);

    int remove(List<S> ids);

    List<T> getAll();

    Page<T> getAll(int page, int size);

    long countAll();

    <U> List<T> findBy(String fieldName, U fieldValue);

    <U> List<T> findByForUpdate(EntityManager em, String fieldName, U fieldValue);

    <U> void removeBy(String fieldName, U fieldValue);

    boolean exists(T entity);

    void inTransaction(Consumer<EntityManager> action);
}