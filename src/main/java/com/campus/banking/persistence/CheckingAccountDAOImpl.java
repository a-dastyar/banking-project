package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.campus.banking.model.CheckingAccount;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Dependent
class CheckingAccountDAOImpl extends AbstractDAO<CheckingAccount, Long>
        implements BankAccountDAO<CheckingAccount> {
            
    private EntityManager entityManager;
    
    @Inject
    public CheckingAccountDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<CheckingAccount> findByAccountNumber(String accountNumber) {
        return findBy("accountNumber", accountNumber).stream()
                .findFirst();
    }

    @Override
    public Optional<CheckingAccount> findByAccountNumberForUpdate(EntityManager em, String accountNumber) {
        return findByForUpdate(em, "accountNumber", accountNumber).stream()
                .findFirst();
    }

    @Override
    public long countByUsername(String username) {
        return withEntityManager(em->{
            var query="""
                    SELECT COUNT(*) 
                      FROM CheckingAccount account 
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
    public Page<CheckingAccount> findByUsername(String username, int page, int size) {
        return withEntityManager(em -> {
            var query = """
                    FROM CheckingAccount account
                    JOIN account.accountHolder user
                   WHERE user.username = :username
                     AND TYPE(account) = :type
                    """;
            var typedQuery = em.createQuery(query, CheckingAccount.class);
            typedQuery.setFirstResult((page - 1) * size);
            typedQuery.setMaxResults(size);
            typedQuery.setParameter("username", username);
            typedQuery.setParameter("type", getType());
            List<CheckingAccount> list = typedQuery.getResultList();
            long countAll = countByUsername(username);
            return new Page<>(list, countAll, page, size);
        });
    }


    @Override
    public boolean exists(CheckingAccount entity) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<CheckingAccount> root = query.from(getType());
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
            Root<CheckingAccount> root = query.from(getType());
            query.select(builder.sumAsDouble(root.get("balance")));
            query.where(builder.gt(root.get("balance"), min));
            var result = em.createQuery(query).getSingleResult();
            return result == null ? 0.0d : result;
        });
    }

    @Override
    protected Class<CheckingAccount> getType() {
        return CheckingAccount.class;
    }

    @Override
    public <U> U withEntityManager(Function<EntityManager, U> action) {
        return action.apply(entityManager);
    }
}