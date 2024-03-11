package com.campus.banking.persistence;

import java.util.Optional;
import java.util.function.Function;

import com.campus.banking.model.CheckingAccount;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CheckingAccountDAOImpl extends AbstractDAO<CheckingAccount, Long>
        implements BankAccountDAO<CheckingAccount> {
    private Database db;

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
            query.select(builder.sum(root.get("balance")));
            query.where(builder.gt(root.get("balance"), min));
            var result = em.createQuery(query).getSingleResult();
            return result == null ? 0 : result;
        });
    }

    @Override
    protected Class<CheckingAccount> getType() {
        return CheckingAccount.class;
    }

    @Override
    public <U> U withEntityManager(Function<EntityManager, U> action) {
        return db.withEntityManager(action);
    }
}