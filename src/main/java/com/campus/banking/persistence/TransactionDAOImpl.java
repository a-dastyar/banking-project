package com.campus.banking.persistence;

import java.util.function.Function;

import com.campus.banking.model.Transaction;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Dependent
class TransactionDAOImpl extends AbstractDAO<Transaction, Long> implements TransactionDAO {

    private EntityManager entityManager;

    @Inject
    public TransactionDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean exists(Transaction entity) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<Transaction> root = query.from(getType());
            query.select(builder.count(root));
            query.where(builder.equal(root.get("id"), entity.getId()));
            return em.createQuery(query).getSingleResult() > 0;
        });
    }

    @Override
    protected Class<Transaction> getType() {
        return Transaction.class;
    }

    @Override
    public <U> U withEntityManager(Function<EntityManager, U> action) {
        return action.apply(entityManager);
    }
}
