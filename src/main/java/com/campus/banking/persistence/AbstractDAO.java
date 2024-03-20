package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.campus.banking.model.BaseModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;

public abstract class AbstractDAO<T extends BaseModel<S>, S> implements DAO<T, S> {

    @Override
    public Optional<T> find(S id) {
        return withEntityManager(em -> Optional.ofNullable((T) em.find(getType(), id)));
    }

    @Override
    public void persist(T entity) {
        inTransaction(em -> transactionalPersist(em, entity));
    }

    @Override
    public void transactionalPersist(EntityManager em, T entity) {
        em.persist(entity);
    }

    @Override
    public void update(T entity) {
        inTransaction(em -> transactionalUpdate(em, entity));
    }

    @Override
    public void transactionalUpdate(EntityManager em, T entity) {
        em.merge(entity);
    }

    @Override
    public void transactionalRemove(EntityManager em, T entity) {
        em.remove(entity);
    }

    @Override
    public void persist(List<T> list) {
        inTransaction(em -> list.forEach(em::persist));
    }

    @Override
    public void update(List<T> list) {
        inTransaction(em -> list.forEach(em::merge));
    }

    @Override
    public List<T> getAll() {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = builder.createQuery(getType());
            Root<T> select = criteriaQuery.from(getType());
            criteriaQuery.where(builder.equal(select.type(), getType()));
            return em.createQuery(criteriaQuery).getResultList();
        });
    }

    @Override
    public Page<T> getAll(int page, int size) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = builder.createQuery(getType());
            Root<T> select = criteriaQuery.from(getType());
            criteriaQuery.where(builder.equal(select.type(), getType()));
            TypedQuery<T> query = em.createQuery(criteriaQuery);
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            List<T> list = query.getResultList();
            long countAll = countAll();
            return new Page<>(list, countAll, page, size);
        });
    }

    @Override
    public long countAll() {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<T> select = query.from(getType());
            query.select(builder.count(select));
            query.where(builder.equal(select.type(), getType()));
            return em.createQuery(query).getSingleResult();
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> List<T> findBy(String fieldName, U fieldValue) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(getType());
            Root<T> select = query.from(getType());
            query.where(builder.equal(select.type(), getType()));
            ParameterExpression<U> parameter = builder.parameter((Class<U>) fieldValue.getClass());
            query.where(builder.equal(select.get(fieldName), parameter));
            TypedQuery<T> typedQuery = em.createQuery(query);
            typedQuery.setParameter(parameter, fieldValue);
            return typedQuery.getResultList();
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> Page<T> findBy(String fieldName, U fieldValue, int page, int size) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(getType());
            Root<T> select = query.from(getType());
            query.where(builder.equal(select.type(), getType()));
            ParameterExpression<U> parameter = builder.parameter((Class<U>) fieldValue.getClass());
            query.where(builder.equal(select.get(fieldName), parameter));
            TypedQuery<T> typedQuery = em.createQuery(query);
            typedQuery.setParameter(parameter, fieldValue);
            typedQuery.setFirstResult((page - 1) * size);
            typedQuery.setMaxResults(size);
            List<T> list = typedQuery.getResultList();
            long countAll = countAll();
            return new Page<>(list, countAll, page, size);
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> List<T> findByForUpdate(EntityManager em, String fieldName, U fieldValue) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(getType());
        Root<T> select = query.from(getType());
        query.where(builder.equal(select.type(), getType()));
        ParameterExpression<U> parameter = builder.parameter((Class<U>) fieldValue.getClass());
        query.where(builder.equal(select.get(fieldName), parameter));
        TypedQuery<T> typedQuery = em.createQuery(query);
        typedQuery.setParameter(parameter, fieldValue);
        typedQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        return typedQuery.getResultList();
    }

    @Override
    public <U> int removeBy(String fieldName, U fieldValue) {
        return inTransactionReturn(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaDelete<T> delete = builder.createCriteriaDelete(getType());
            Root<T> select = delete.from(getType());
            delete.where(builder.and(
                    builder.equal(select.get(fieldName), fieldValue),
                    builder.equal(select.type(), getType())));
            Query deleteQuery = em.createQuery(delete);
            var result = deleteQuery.executeUpdate();
            em.flush();
            em.clear();
            return result;
        });
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

    @Override
    public <U> U inTransactionReturn(Function<EntityManager, U> action) {
        return withEntityManager(em -> {
            var trx = em.getTransaction();
            try {
                trx.begin();
                var result = action.apply(em);
                trx.commit();
                return result;
            } catch (RuntimeException e) {
                trx.rollback();
                throw e;
            }
        });
    }

    protected abstract Class<T> getType();

}