package com.campus.banking.service;

import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.campus.banking.exception.DuplicatedException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.Role;
import com.campus.banking.model.User;
import com.campus.banking.persistence.Page;
import com.campus.banking.persistence.UserDAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
    public void add(@NotNull @Valid User user) {
        if (dao.exists(user)) {
            log.debug("User already exists!");
            throw new DuplicatedException();
        }
        user.setId(null);
        user.setPassword(hashService.hashOf(user.getPassword()));
        dao.inTransaction(em -> dao.transactionalPersist(em, user));
    }

    @Override
    public void signup(@NotNull @Valid User user) {
        if (dao.exists(user)) {
            log.debug("User already exists!");
            throw new DuplicatedException();
        }
        user.setId(null);
        user.setRoles(Set.of(Role.MEMBER));
        user.setPassword(hashService.hashOf(user.getPassword()));
        dao.inTransaction(em -> dao.transactionalPersist(em, user));
    }

    @Override
    public User getByUsername(@NotNull @NotBlank String username) {
        var user = this.dao.findBy("username", username).stream().findFirst();
        return user.orElseThrow(NotFoundException::new);
    }

    @Override
    public void update(@NotNull @Valid User user) {
        var found = getByUsername(user.getUsername());
        user.setId(found.getId());
        user.setPassword(found.getPassword());
        dao.inTransaction(em -> dao.transactionalUpdate(em, user));
    }

    @Override
    public Page<User> getAll(@Positive int page) {
        log.debug("GetAll for page[{}]", page);
        return dao.getAll(page, maxPageSize);
    }

    @Override
    public void setupAdminAccount() {
        if (dao.countAll() == 0) {
            var admin = User.builder()
                    .username("admin")
                    .password("admin")
                    .email("admin@bank.co")
                    .roles(Set.of(Role.ADMIN))
                    .build();
            add(admin);
        }
    }
}
