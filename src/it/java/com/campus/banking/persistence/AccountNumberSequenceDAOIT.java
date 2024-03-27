package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractDatabaseIT;
import com.campus.banking.model.AccountNumberSequence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountNumberSequenceDAOIT extends AbstractDatabaseIT {

    private AccountNumberSequenceDAO dao;

    private EntityManager em;

    @BeforeEach
    void setup() {
        log.debug("setup");
        em = super.emf.createEntityManager();
        dao = new AccountNumberSequenceDAOImpl(em);
    }

    @AfterEach
    void teardown() {
        log.debug("teardown");
        em.close();
    }

    @Test
    void persist_withValidSequence_shouldSave() {
        var seq = AccountNumberSequence.builder()
                .sequence(0L)
                .year(2024)
                .build();
        dao.persist(seq);
        var list = new ArrayList<AccountNumberSequence>();
        assertThat(seq.getId()).isNotNull();
        dao.inTransaction(em -> list.add(dao.findForUpdate(em)));
        assertThat(list.getFirst()).isEqualTo(seq);
    }

    @Test
    void findForUpdate_withNoSequence_shouldReturnFail() {
        assertThatThrownBy(() -> dao.inTransaction(em -> dao.findForUpdate(em)))
                .isInstanceOf(NoResultException.class);
    }

    @Test
    void findForUpdate_withValidSequence_shouldFind() {
        var seq = AccountNumberSequence.builder()
                .sequence(0L)
                .year(2024)
                .build();
        dao.persist(seq);
        var list = new ArrayList<AccountNumberSequence>();
        assertThat(seq.getId()).isNotNull();
        dao.inTransaction(em -> list.add(dao.findForUpdate(em)));
        assertThat(list.getFirst()).isEqualTo(seq);
    }

    @Test
    void transactionalUpdate_withValidSequence_shouldUpdate() {
        var seq = AccountNumberSequence.builder()
                .sequence(0L)
                .year(2024)
                .build();
        dao.persist(seq);
        var list = new ArrayList<AccountNumberSequence>();
        assertThat(seq.getId()).isNotNull();

        seq.setYear(2025);
        dao.inTransaction(em -> dao.transactionalUpdate(em, seq));

        dao.inTransaction(em -> list.add(dao.findForUpdate(em)));
        assertThat(list.getFirst().getYear()).isEqualTo(2025);
    }

    @Test
    void exists_withValidSequence_shouldUpdate() {
        var seq = AccountNumberSequence.builder()
                .sequence(0L)
                .year(2024)
                .build();
        dao.persist(seq);
        assertThat(seq.getId()).isNotNull();

        var exists = dao.exists();

        assertThat(exists).isTrue();
    }

    @Test
    void inTransaction_withException_shouldRollBack() {
        var seq = AccountNumberSequence.builder()
                .sequence(0L)
                .year(2024)
                .build();
        assertThatThrownBy(() -> {
            dao.inTransaction(em -> {
                em.persist(seq);
                throw new RuntimeException();
            });
        }).isInstanceOf(RuntimeException.class);
    }
}
