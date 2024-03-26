package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.campus.banking.model.SavingAccount;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Dependent
class SavingAccountDAOImpl extends AbstractDAO<SavingAccount, Long> implements SavingAccountDAO {

    private EntityManager entityManager;

    @Inject
    public SavingAccountDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<SavingAccount> findByAccountNumber(String accountNumber) {
        return findBy("accountNumber", accountNumber).stream()
                .findFirst();
    }

    @Override
    public Optional<SavingAccount> findByAccountNumberForUpdate(EntityManager em, String accountNumber) {
        return findByForUpdate(em, "accountNumber", accountNumber).stream()
                .findFirst();
    }


    @Override
    public long countByUsername(String username) {
        return withEntityManager(em->{
            var query="""
                    SELECT COUNT(*) 
                      FROM SavingAccount account 
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
    public Page<SavingAccount> findByUsername(String username, int page, int size) {
        return withEntityManager(em -> {
            var query = """
                    FROM SavingAccount account
                    JOIN account.accountHolder user
                   WHERE user.username = :username
                     AND TYPE(account) = :type
                    """;
            var typedQuery = em.createQuery(query, SavingAccount.class);
            typedQuery.setFirstResult((page - 1) * size);
            typedQuery.setMaxResults(size);
            typedQuery.setParameter("username", username);
            typedQuery.setParameter("type", getType());
            List<SavingAccount> list = typedQuery.getResultList();
            long countAll = countByUsername(username);
            return new Page<>(list, countAll, page, size);
        });
    }


    @Override
    public boolean exists(SavingAccount entity) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<SavingAccount> root = query.from(getType());
            query.select(builder.count(root));
            query.where(builder.or(
                    builder.equal(root.get("id"), entity.getId()),
                    builder.equal(root.get("accountNumber"), entity.getAccountNumber())));
            return em.createQuery(query).getSingleResult() > 0;
        });
    }

    @Override
    public void applyInterest() {
        var update = """
                UPDATE bank_accounts account
                  JOIN saving_accounts saving
                    ON saving.id = account.id
                   SET account.balance = account.balance + (account.balance * saving.interest_rate/100.0)
                  """;
        var insertTransactions = """
                INSERT INTO transactions(
                    type,
                    amount,
                    bank_account_id,
                    date)
                SELECT 'INTEREST',
                        account.balance * saving.interest_rate / 100.0,
                        account.id,
                        NOW()
                  FROM saving_accounts saving
                  JOIN bank_accounts account
                    ON saving.id = account.id
                    """;
        inTransaction(em -> {
            Query query = em.createQuery("FROM SavingAccount");
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            query.getResultList();
            em.createNativeQuery(insertTransactions).executeUpdate();
            em.createNativeQuery(update).executeUpdate();
        });
    }

    @Override
    public double sumBalanceHigherThan(double min) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Double> query = builder.createQuery(Double.class);
            Root<SavingAccount> root = query.from(getType());
            query.select(builder.sumAsDouble(root.get("balance")));
            query.where(builder.gt(root.get("balance"), min));
            var result = em.createQuery(query).getSingleResult();
            return result == null ? 0.0d : result;
        });
    }

    @Override
    protected Class<SavingAccount> getType() {
        return SavingAccount.class;
    }

    @Override
    public <U> U withEntityManager(Function<EntityManager, U> action) {
        return action.apply(entityManager);
    }
}