package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.campus.banking.model.BankAccount;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Dependent
class BankAccountDAOImpl extends AbstractDAO<BankAccount, Long> implements BankAccountDAO<BankAccount> {

    private EntityManager entityManager;

    @Inject
    public BankAccountDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

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
    public List<BankAccount> findByUsername(String username) {
        return withEntityManager(em -> {
            var query = em.createQuery("FROM BankAccount account JOIN account.accountHolder user where user.username = :username",
                    BankAccount.class);
            query.setParameter("username", username);
            return query.getResultList();
        });
    }

    @Override
    public boolean exists(BankAccount entity) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<BankAccount> root = query.from(getType());
            query.select(builder.count(root));
            query.where(builder.or(
                    builder.equal(root.get("id"), entity.getId()),
                    builder.equal(root.get("accountNumber"), entity.getAccountNumber())));
            return em.createQuery(query).getSingleResult() > 0;
        });
    }

    @Override
    public double sumBalanceHigherThan(double min) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Double> query = builder.createQuery(Double.class);
            Root<BankAccount> root = query.from(getType());
            query.select(builder.sumAsDouble(root.get("balance")));
            query.where(builder.gt(root.get("balance"), min));
            var result = em.createQuery(query).getSingleResult();
            return result == null ? 0.0d : result;
        });
    }

    @Override
    protected Class<BankAccount> getType() {
        return BankAccount.class;
    }

    @Override
    public <U> U withEntityManager(Function<EntityManager, U> action) {
        return action.apply(entityManager);
    }

}