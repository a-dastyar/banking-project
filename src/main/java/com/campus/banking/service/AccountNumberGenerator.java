package com.campus.banking.service;

import com.campus.banking.model.AccountType;

import jakarta.persistence.EntityManager;

public interface AccountNumberGenerator {

    String transactionalGenerate(EntityManager em, AccountType type);

    void setupNumberGenerator();

}