package com.campus.banking.persistence;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public interface Database {

    EntityManager getEntityManager();

    EntityManagerFactory getEntityManagerFactory();

    void closeEntityManagerFactory();
}