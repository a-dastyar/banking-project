package com.campus.banking.persistence;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
        return Optional.ofNullable((T) getEntityManager().find(getType(), id));
    }

    @Override
    public void persist(T entity) {
        inTransaction(em -> em.persist(entity));
    }

    @Override
    public void transactionalPersist(EntityManager em, T entity) {
        em.persist(entity);
    }

    @Override
    public void update(T entity) {
        try (var em = getEntityManager()) {
            em.merge(entity);
        }
    }

    @Override
    public void transactionalUpdate(EntityManager em, T entity) {
        em.merge(entity);
    }

    @Override
    public void remove(T entity) {
        getEntityManager().remove(entity);
    }

    @Override
    public List<T> find(List<S> ids) {
        try (var em = getEntityManager()) {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<T> select = builder.createQuery(getType());
            Root<T> root = select.from(getType());
            select.where(root.in(ids));
            return em.createQuery(select).getResultList();
        }
    }

    @Override
    public void persist(List<T> list) {
        inTransaction(em -> list.forEach(em::persist));
    }

    @Override
    public void update(List<T> list) {
        try (var em = getEntityManager()) {
            list.forEach(em::merge);
        }
    }

    @Override
    public int remove(List<S> ids) {
        try (var em = getEntityManager()) {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaDelete<T> delete = builder.createCriteriaDelete(getType());
            Root<T> root = delete.from(getType());
            delete.where(root.in(ids));
            int deleteCount = em.createQuery(delete).executeUpdate();
            return deleteCount;
        }
    }

    @Override
    public List<T> getAll() {
        try (var em = getEntityManager()) {
            CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(getType());
            criteriaQuery.from(getType());
            return em.createQuery(criteriaQuery).getResultList();
        }
    }

    @Override
    public Page<T> getAll(int page, int size) {
        try (var em = getEntityManager()) {
            CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(getType());
            criteriaQuery.from(getType());
            TypedQuery<T> query = em.createQuery(criteriaQuery);
            query.setFirstResult((page - 1) * size);
            query.setMaxResults((page) * size);
            List<T> list = query.getResultList();
            long countAll = countAll();
            return new Page<>(list, countAll, page, size);
        }
    }

    @Override
    public long countAll() {
        try (var em = getEntityManager()) {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            query.select(builder.count(query.from(getType())));
            return em.createQuery(query).getSingleResult();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> List<T> findBy(String fieldName, U fieldValue) {
        try (var em = getEntityManager()) {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(getType());
            Root<T> select = query.from(getType());
            ParameterExpression<U> parameter = builder.parameter((Class<U>) fieldValue.getClass());
            query.where(builder.equal(select.get(fieldName), parameter));
            TypedQuery<T> typedQuery = em.createQuery(query);
            typedQuery.setParameter(parameter, fieldValue);
            return typedQuery.getResultList();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> List<T> findByForUpdate(EntityManager em, String fieldName, U fieldValue) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(getType());
        Root<T> select = query.from(getType());
        ParameterExpression<U> parameter = builder.parameter((Class<U>) fieldValue.getClass());
        query.where(builder.equal(select.get(fieldName), parameter));
        TypedQuery<T> typedQuery = em.createQuery(query);
        typedQuery.setParameter(parameter, fieldValue);
        typedQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        return typedQuery.getResultList();
    }

    @Override
    public <U> void removeBy(String fieldName, U fieldValue) {
        try (var em = getEntityManager()) {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaDelete<T> delete = builder.createCriteriaDelete(getType());
            Root<T> select = delete.from(getType());
            delete.where(builder.equal(select.get(fieldName), fieldValue));
            Query deleteQuery = em.createQuery(delete);
            deleteQuery.executeUpdate();
        }
    }

    @Override
    public void inTransaction(Consumer<EntityManager> action) {
        try (var em = getEntityManager()) {
            var trx = em.getTransaction();
            try {
                trx.begin();
                action.accept(em);
                trx.commit();
            } catch (RuntimeException e) {
                trx.rollback();
                throw e;
            }
        }
    }

    protected abstract Class<T> getType();

    protected abstract EntityManager getEntityManager();
}