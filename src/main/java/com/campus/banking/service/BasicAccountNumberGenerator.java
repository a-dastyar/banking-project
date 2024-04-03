package com.campus.banking.service;

import java.time.LocalDate;

import com.campus.banking.model.AccountNumberSequence;
import com.campus.banking.model.AccountType;
import com.campus.banking.persistence.AccountNumberSequenceDAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
class BasicAccountNumberGenerator implements AccountNumberGenerator {

    private final AccountNumberSequenceDAO dao;

    @Inject
    public BasicAccountNumberGenerator(AccountNumberSequenceDAO dao) {
        this.dao = dao;
    }

    @Override
    public String transactionalGenerate(EntityManager em, AccountType type) {
        var accountNumber = dao.findForUpdate(em);
        var year = LocalDate.now().getYear();
        if (accountNumber.getYear() != year) {
            accountNumber.setYear(year);
            accountNumber.setSequence(0L);
        }
        var sequenceVal = accountNumber.getSequence();
        var number = String.format("%s%03d%08d", year, type.value, sequenceVal + 1);
        accountNumber.setSequence(sequenceVal + 1);
        dao.transactionalUpdate(em, accountNumber);
        return number;
    }

    @Override
    public void setupNumberGenerator() {
        if (!dao.exists()) {
            var seq = AccountNumberSequence.builder()
                    .sequence(0L)
                    .year(LocalDate.now().getYear())
                    .build();
            dao.persist(seq);
        }
    }

}
