package com.campus.banking.persistence;

import java.util.function.Function;

import jakarta.persistence.EntityManager;

public interface Database {

    <U> U withEntityManager(Function<EntityManager, U> action);

    void closeEntityManagerFactory();
}