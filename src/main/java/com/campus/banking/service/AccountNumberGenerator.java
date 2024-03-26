package com.campus.banking.service;

import jakarta.persistence.EntityManager;

public interface AccountNumberGenerator {

    static enum AccountType {
        BANK(1), SAVING(2), CHECKING(3);

        public final int value;

        AccountType(int id) {
            this.value = id;
        }
    }

    String transactionalGenerate(EntityManager em, AccountType type);

    void setupNumberGenerator();

}