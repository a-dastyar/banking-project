package com.campus.banking.persistence;

import java.util.function.Consumer;
import java.util.function.Function;

import com.campus.banking.model.Transaction;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;

@Dependent
class TransactionDAOImpl implements TransactionDAO {

    private EntityManager entityManager;

    @Inject
    public TransactionDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void transactionalPersist(EntityManager em, Transaction entity) {
        em.persist(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> Page<Transaction> findByOrdered(String fieldName, U fieldValue, int page, int size, String orderField,
            Order order) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Transaction> query = builder.createQuery(Transaction.class);
            Root<Transaction> select = query.from(Transaction.class);
            query.where(builder.equal(select.type(),Transaction.class));
            if (orderField != null) {
                var by = switch (order) {
                    case ASC -> builder.asc(select.get(orderField));
                    case DESC -> builder.desc(select.get(orderField));
                };
                query.orderBy(by);
            }
            ParameterExpression<U> parameter = builder.parameter((Class<U>) fieldValue.getClass());
            query.where(builder.equal(select.get(fieldName), parameter));
            TypedQuery<Transaction> typedQuery = em.createQuery(query);
            typedQuery.setParameter(parameter, fieldValue);
            typedQuery.setFirstResult((page - 1) * size);
            typedQuery.setMaxResults(size);
            var list = typedQuery.getResultList();
            long countAll = countBy(fieldName, fieldValue);
            return new Page<>(list, countAll, page, size);
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> long countBy(String fieldName, U fieldValue) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<Transaction> select = query.from(Transaction.class);
            query.select(builder.count(select));
            query.where(builder.equal(select.type(), Transaction.class));
            ParameterExpression<U> parameter = builder.parameter((Class<U>) fieldValue.getClass());
            query.where(builder.equal(select.get(fieldName), parameter));
            TypedQuery<Long> typedQuery = em.createQuery(query);
            typedQuery.setParameter(parameter, fieldValue);
            return typedQuery.getSingleResult();
        });
    }

    private <U> U withEntityManager(Function<EntityManager, U> action) {
        return action.apply(entityManager);
    }

    @Override
    public void inTransaction(Consumer<EntityManager> action) {
        withEntityManager(em -> {
            var trx = em.getTransaction();
            try {
                trx.begin();
                action.accept(em);
                trx.commit();
            } catch (RuntimeException e) {
                trx.rollback();
                throw e;
            }
            return null;
        });
    }
}
