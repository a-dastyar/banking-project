package com.campus.banking.persistence;

import com.campus.banking.model.Transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TransactionDAOImpl extends AbstractDAO<Transaction, Long> implements TransactionDAO {

    private Database db;

    @Override
    public boolean exists(Transaction entity) {
        try (var em = getEntityManager()) {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<Transaction> root = query.from(getType());
            query.select(builder.count(root));
            query.where(builder.equal(root.get("id"), entity.getId()));
            return em.createQuery(query).getSingleResult() > 0;
        }
    }

    @Override
    protected Class<Transaction> getType() {
        return Transaction.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return db.getEntityManager();
    }

}
