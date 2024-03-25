package com.campus.banking.persistence;

import com.campus.banking.model.AccountNumberSequence;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Dependent
class AccountNumberSequenceDAOImpl implements AccountNumberSequenceDAO {

    private EntityManager em;

    @Override
    public void persist(AccountNumberSequence accountNumberSequence) {
        var trx = em.getTransaction();
        try {
            trx.begin();
            em.persist(accountNumberSequence);
            trx.commit();
        } catch (RuntimeException e) {
            trx.rollback();
            throw e;
        }
    }

    @Inject
    public AccountNumberSequenceDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public AccountNumberSequence findForUpdate(EntityManager em) {
        var query = em.createQuery("FROM AccountNumberSequence", AccountNumberSequence.class);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        return query.getSingleResult();
    }

    @Override
    public void transactionalUpdate(EntityManager em, AccountNumberSequence accountNumber) {
        em.merge(accountNumber);
    }

    @Override
    public boolean exists() {
        var query = em.createQuery("SELECT COUNT(*) FROM AccountNumberSequence", Long.class);
        return query.getSingleResult() > 0;
    }

}
