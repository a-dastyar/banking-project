package com.campus.banking.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.campus.banking.exception.DuplicatedException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.User;
import com.campus.banking.persistence.Page;
import com.campus.banking.persistence.UserDAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
class UserServiceImpl implements UserService {

    private UserDAO dao;
    private HashService hashService;
    private int maxPageSize;

    @Inject
    public UserServiceImpl(UserDAO dao, HashService hashService,
            @ConfigProperty(name = "app.pagination.max_size") int maxPageSize) {
        this.dao = dao;
        this.hashService = hashService;
        this.maxPageSize = maxPageSize;
    }

    @Override
    public User getById(@Positive long id) {
        var user = this.dao.find(id);
        return user.orElseThrow(NotFoundException::new);
    }

    @Override
    public void removeById(@Positive long id) {
        var user = getById(id);
        dao.inTransaction(em -> dao.transactionalRemove(em, user));
    }

    @Override
    public void addUser(@NotNull @Valid User user) {
        if (dao.exists(user)) {
            log.debug("User already exists!");
            throw new DuplicatedException();
        }
        log.debug("Adding user");
        user.setPassword(hashService.hashOf(user.getPassword()));
        dao.inTransaction(em -> dao.transactionalPersist(em, user));
    }

    @Override
    public void updateUser(@NotNull @Valid User user) {
        var found = getById(user.getId());
        if (!user.getPassword().equals(found.getPassword()))
            user.setPassword(hashService.hashOf(user.getPassword()));
        dao.inTransaction(em -> dao.transactionalUpdate(em, user));
    }

    @Override
    public Page<User> getAll(@Min(1) int page) {
        log.debug("GetAll for page[{}]", page);
        return dao.getAll(page, maxPageSize);
    }

}
