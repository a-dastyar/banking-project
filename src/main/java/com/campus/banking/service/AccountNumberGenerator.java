package com.campus.banking.service;

import jakarta.persistence.EntityManager;

public interface AccountNumberGenerator {

    String transactionalGenerate(EntityManager em);

    void setupNumberGenerator();
    
} 