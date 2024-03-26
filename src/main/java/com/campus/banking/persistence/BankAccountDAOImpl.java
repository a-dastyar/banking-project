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
    public long countByUsername(String username) {
        return withEntityManager(em->{
            var query="""
                    SELECT COUNT(*) 
                      FROM BankAccount account 
                      JOIN account.accountHolder user 
                     WHERE user.username = :username 
                       AND TYPE(account) = :type
                    """;
            var typedQuery = em.createQuery(query, Long.class);
            typedQuery.setParameter("username", username);
            typedQuery.setParameter("type", getType());
            return typedQuery.getSingleResult();
        });
    }

    @Override
    public Page<BankAccount> findByUsername(String username, int page, int size) {
        return withEntityManager(em -> {
            var query = """
                    FROM BankAccount account
                    JOIN account.accountHolder user
                   WHERE user.username = :username
                     AND TYPE(account) = :type
                    """;
            var typedQuery = em.createQuery(query, BankAccount.class);
            typedQuery.setFirstResult((page - 1) * size);
            typedQuery.setMaxResults(size);
            typedQuery.setParameter("username", username);
            typedQuery.setParameter("type", getType());
            List<BankAccount> list = typedQuery.getResultList();
            long countAll = countByUsername(username);
            return new Page<>(list, countAll, page, size);
        });
    }

    @Override
    public boolean exists(BankAccount entity) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<BankAccount> select = query.from(getType());
            query.select(builder.count(select));
            query.where(builder.and(
                    builder.or(
                            builder.equal(select.get("id"), entity.getId()),
                            builder.equal(select.get("accountNumber"), entity.getAccountNumber())),
                    builder.equal(select.type(), getType())));
            return em.createQuery(query).getSingleResult() > 0;
        });
    }

    @Override
    public double sumBalanceHigherThan(double min) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Double> query = builder.createQuery(Double.class);
            Root<BankAccount> select = query.from(getType());
            query.select(builder.sumAsDouble(select.get("balance")));
            query.where(builder.and(
                    builder.gt(select.get("balance"), min),
                    builder.equal(select.type(), getType())));
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