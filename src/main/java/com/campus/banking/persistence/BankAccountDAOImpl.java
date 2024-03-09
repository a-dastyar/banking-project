package com.campus.banking.persistence;

import java.util.Optional;

import com.campus.banking.model.BankAccount;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BankAccountDAOImpl extends AbstractDAO<BankAccount, Long> implements BankAccountDAO<BankAccount> {
    private Database db;

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return findBy("accountNumber", accountNumber).stream()
                .findFirst();
    }

    @Override
    public Optional<BankAccount> findByAccountNumberForUpdate(EntityManager em, String accountNumber) {
        return findByForUpdate(em, "accountNumber", accountNumber).stream()
                .findFirst();
    }

    @Override
    public boolean exists(BankAccount entity) {
        try (var em = getEntityManager()) {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<BankAccount> root = query.from(getType());
            query.select(builder.count(root));
            query.where(builder.or(
                    builder.equal(root.get("id"), entity.getId()),
                    builder.equal(root.get("account_number"), entity.getAccountNumber())));
            return em.createQuery(query).getSingleResult() > 0;
        }
    }

    @Override
    public double sumBalanceHigherThan(double min) {
        try (var em = getEntityManager()) {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Double> query = builder.createQuery(Double.class);
            Root<BankAccount> root = query.from(getType());
            query.select(builder.sum(root.get("balance")));
            query.where(builder.gt(root.get("balance"), min));
            return em.createQuery(query).getSingleResult();
        }
    }

    @Override
    protected Class<BankAccount> getType() {
        return BankAccount.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return db.getEntityManager();
    }
}