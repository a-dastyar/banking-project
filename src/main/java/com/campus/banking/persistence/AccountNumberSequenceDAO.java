package com.campus.banking.persistence;

import java.util.function.Consumer;

import com.campus.banking.model.AccountNumberSequence;

import jakarta.persistence.EntityManager;

public interface AccountNumberSequenceDAO {

    void persist(AccountNumberSequence accountNumberSequence);

    AccountNumberSequence findForUpdate(EntityManager em);

    void transactionalUpdate(EntityManager em, AccountNumberSequence accountNumber);

    boolean exists();
    
    void inTransaction(Consumer<EntityManager> action);
} 