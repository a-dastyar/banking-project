package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractDatabaseIT;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.TransactionType;
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
    void transactionalPersist_withSuccessfulTransaction_shouldSave() {
        var trx = generateTransactions().findFirst().get();
        dao.inTransaction(em -> dao.transactionalPersist(em, trx));
        assertThat(trx.getId()).isNotNull();
    }

    @Test
    void findByOrdered_withOrderByAmountASC_shouldReturnPage() {
        var accounts = generateTransactions()
                .peek(acc -> acc.setAmount(10.0))
                .limit(5)
                .toList();
        accounts.getLast().setAmount(15.0);
        dao.inTransaction(em->accounts.forEach(em::persist));
        var page = dao.findByOrdered("amount", 10.0, 1, 2,"date",Order.ASC);
        assertThat(page.total()).isEqualTo(4);
        assertThat(page.list().size()).isEqualTo(2);
        assertThat(page.list().getFirst().getDate()).isEqualTo(accounts.getFirst().getDate());
    }

    @Test
    void findByOrdered_withOrderByAmountDESC_shouldReturnPage() {
        var accounts = generateTransactions()
                .peek(acc -> acc.setAmount(10.0))
                .limit(5)
                .toList();
        accounts.getLast().setAmount(15.0);
        dao.inTransaction(em->accounts.forEach(em::persist));
        var page = dao.findByOrdered("amount", 10.0, 1, 2,"date",Order.DESC);
        assertThat(page.total()).isEqualTo(4);
        assertThat(page.list().size()).isEqualTo(2);
        assertThat(page.list().getFirst().getDate()).isEqualTo(accounts.get(3).getDate());
    }

    @Test
    void countBy_withMultipleTransactions_shouldReturnCount() {
        var accounts = generateTransactions()
                .peek(acc -> acc.setAmount(10.0))
                .limit(5)
                .toList();
        accounts.getLast().setAmount(15.0);
        dao.inTransaction(em->accounts.forEach(em::persist));
        var page = dao.countBy("amount", 10.0);
        assertThat(page).isEqualTo(4);
    }

    @Test
    void inTransaction_withException_shouldRollBack() {
        var account = generateTransactions().findFirst().get();
        assertThatThrownBy(() -> {
            dao.inTransaction(em -> {
                em.persist(account);
                throw new RuntimeException();
            });
        }).isInstanceOf(RuntimeException.class);
    }

    protected Stream<Transaction> generateTransactions() {
        return IntStream.range(0, 1_000_000)
                .mapToObj(this::createAccount);
    }

    private Transaction createAccount(int i) {
        return Transaction.builder()
                .account(account)
                .amount(i * 10 + 400)
                .type(TransactionType.DEPOSIT)
                .build();
    }
}
