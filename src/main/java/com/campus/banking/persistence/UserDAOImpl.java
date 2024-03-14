package com.campus.banking.persistence;

import java.util.function.Function;

import com.campus.banking.model.User;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Dependent
class UserDAOImpl extends AbstractDAO<User, Long> implements UserDAO {

    private EntityManager entityManager;

    @Inject
    public UserDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <U> U withEntityManager(Function<EntityManager, U> action) {
        return action.apply(entityManager);
    }

    @Override
    public Class<User> getType() {
        return User.class;
    }

    @Override
    public boolean exists(User entity) {
        return withEntityManager(em -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<User> root = query.from(getType());
            query.select(builder.count(root));
            query.where(builder.or(
                    builder.equal(root.get("id"), entity.getId()),
                    builder.equal(root.get("username"), entity.getUsername()),
                    builder.equal(root.get("email"), entity.getEmail())));
            return em.createQuery(query).getSingleResult() > 0;
        });
    }

}
