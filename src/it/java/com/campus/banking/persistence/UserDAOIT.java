package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractDatabaseIT;
import com.campus.banking.model.User;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDAOIT extends AbstractDatabaseIT {

    private UserDAO dao;

    private EntityManager em;

    @BeforeEach
    void setup() {
        em = super.emf.createEntityManager();
        dao = new UserDAOImpl(em);
    }

    @AfterEach
    void teardown() {
        log.debug("teardown");
        em.close();
    }

    @Test
    void persist_withValidUser_shouldSave() {
        var user = User.builder()
                .username("test")
                .email("test@test.test")
                .password("test")
                .build();
        dao.persist(user);
        assertThat(dao.exists(user)).isTrue();
    }

    @Test
    void persistList_withValidUser_shouldSave() {
        var user = User.builder()
                .username("test")
                .email("test@test.test")
                .password("test")
                .build();
        var list = List.of(
            user,
            user.withUsername("test2").withEmail("test2@test.test"),
            user.withUsername("test3").withEmail("test3@test.test")
        );
        dao.persist(list);
        assertThat(dao.countAll()).isEqualTo(3);
    }

}
