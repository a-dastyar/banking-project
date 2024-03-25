package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractDatabaseIT;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.User;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionDAOIT extends AbstractDatabaseIT {

    private TransactionDAO dao;

    private EntityManager em;

    private BankAccount account;

    @BeforeEach
    void setup() {
        log.debug("setup");
        em = super.emf.createEntityManager();
        dao = new TransactionDAOImpl(em);
        var user = User.builder()
                .username("test")
                .password("test")
                .email("test@test.test")
                .build();
        account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(100000).build();
        dao.inTransaction(em -> em.persist(user));
        dao.inTransaction(em -> em.persist(account));
    }

    @AfterEach
    void teardown() {
        log.debug("teardown");
        em.close();
    }

    @Test
    void persist_withValidAccount_shouldSave() {
        var trx = Transaction.builder()
                .account(account)
                .amount(100)
                .build();
        dao.persist(trx);
        var found = dao.find(trx.getId());
        assertThat(found.get().getAmount()).isEqualTo(trx.getAmount());
    }

    @Test
    void persistList_withValidAccount_shouldSave() {
        var trx = Transaction.builder()
                .account(account)
                .amount(100)
                .build();
        var list = List.of(
                trx,
                trx.withAmount(200),
                trx.withAmount(300),
                trx.withAmount(400));
        dao.persist(list);
        var sum = dao.getAll().stream().mapToDouble(Transaction::getAmount).sum();
        assertThat(sum).isEqualTo(1000.0);
    }

    @Test
    void exists_withExisting_shouldReturnTrue() {
        var trx = Transaction.builder()
                .account(account)
                .amount(100)
                .build();
        var found = dao.exists(trx);
        assertThat(found).isFalse();
        dao.persist(trx);
        found = dao.exists(trx);
        assertThat(found).isTrue();

    }
}
