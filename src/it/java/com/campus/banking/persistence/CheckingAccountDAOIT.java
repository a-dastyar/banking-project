package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractDatabaseIT;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.User;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckingAccountDAOIT extends AbstractDatabaseIT {

    private BankAccountDAO<CheckingAccount> dao;

    private EntityManager em;

    private User user;

    @BeforeEach
    void setup() {
        log.debug("setup");
        em = super.emf.createEntityManager();
        dao = new CheckingAccountDAOImpl(em);
        user = User.builder()
                .username("test")
                .password("test")
                .email("test@test.test")
                .build();
        dao.inTransaction(em -> em.persist(user));
    }

    @AfterEach
    void teardown() {
        log.debug("teardown");
        em.close();
    }

    @Test
    void persist_withNullAccountNumber_shouldFail() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        assertThatThrownBy(() -> dao.persist(account))
                .hasMessageContaining("null");
    }

    @Test
    void persist_withValidAccount_shouldSave() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertCheckingAccountEqual(found, account);
    }

    @Test
    void persistList_withNullAccountNumber_shouldFail() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        assertThatThrownBy(() -> dao.persist(List.of(account)))
                .hasMessageContaining("null");
    }

    @Test
    void persistList_withValidAccount_shouldSave() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(List.of(account));
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertCheckingAccountEqual(found, account);
    }

    @Test
    void persistList_withMultipleAccount_shouldSave() {
        var list = List.of(
                CheckingAccount.builder().balance(10)
                        .accountHolder(user).accountNumber("4000").build(),
                CheckingAccount.builder().balance(10)
                        .accountHolder(user).accountNumber("5000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("6000").build());
        dao.persist(list);
        var found = dao.findBy("balance", 10.0);
        assertThat(found.size()).isEqualTo(2);
    }

    @Test
    void find_withNoAccount_shouldReturnEmpty() {
        var found = dao.find(1L);
        assertThat(found).isEmpty();
    }

    @Test
    void find_withAccountId_shouldReturnAccount() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertCheckingAccountEqual(found, account);
    }

    @Test
    void transactionalRemove_withAccount_shouldRemove() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        dao.inTransaction(em -> {
            var found = dao.findByAccountNumberForUpdate(em, account.getAccountNumber()).get();
            dao.transactionalRemove(em, found);
        });
        var found = dao.find(account.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void getAll_withNoAccount_shouldReturnEmpty() {
        var found = dao.getAll();
        assertThat(found).isEmpty();
    }

    @Test
    void getAll_withMultipleAccounts_shouldReturnAccounts() {
        var list = List.of(
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("4000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("5000").build());
        dao.persist(list);
        var found = dao.getAll();
        assertThat(found.size()).isEqualTo(2);
    }

    @Test
    void getAllPaginated_withMultipleAccounts_shouldReturnPage() {
        var list = List.of(
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("4000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("6000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("7000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("8000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("9000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("10000").build());
        dao.persist(list);
        var found = dao.getAll(2, 2);
        assertThat(found.total()).isEqualTo(6);
        assertThat(found.list().size()).isEqualTo(2);
        assertThat(found.list()).map(CheckingAccount::getAccountNumber).contains("7000", "8000");
    }

    @Test
    void countAll_withMultipleAccounts_shouldReturnCount() {
        var list = List.of(
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("4000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("6000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("7000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("8000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("9000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("10000").build());
        dao.persist(list);
        var count = dao.countAll();
        assertThat(count).isEqualTo(6);
    }

    @Test
    void exists_withSameAccount_shouldReturnTrue() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        var exists = dao.exists(account);
        assertThat(exists).isTrue();
    }

    @Test
    void exists_withSameAccountNumber_shouldReturnTrue() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        var newAccount = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        var exists = dao.exists(newAccount);
        assertThat(exists).isTrue();
    }

    @Test
    void exists_withDifferentAccountNumber_shouldReturnTrue() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        var newAccount = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        var exists = dao.exists(newAccount);
        assertThat(exists).isFalse();
    }

    @Test
    void update_withAccount_shouldUpdate() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        account.setBalance(30);
        dao.update(account);
        var found = dao.find(account.getId()).get();
        assertThat(found.getBalance()).isEqualTo(30);
    }

    @Test
    void findByAccountNumber_withNoAccount_shouldReturnEmpty() {
        var found = dao.findByAccountNumber("3000");
        assertThat(found).isEmpty();
    }

    @Test
    void findByAccountNumber_withAccountNumber_shouldReturnAccount() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();
        assertCheckingAccountEqual(found, account);
    }

    @Test
    void findBy_withNoAccount_shouldReturnEmpty() {
        var found = dao.findBy("balance", 10.0);
        assertThat(found).isEmpty();
    }

    @Test
    void findBy_withOneMatchingAccount_shouldReturnAccounts() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        var found = dao.findBy("balance", account.getBalance());
        assertThat(found).isNotEmpty();
    }

    @Test
    void findBy_withMultipleMatchingAccount_shouldReturnAccounts() {
        var list = List.of(
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("4000").balance(10).build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("6000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("7000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("8000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("9000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("10000").build());
        dao.persist(list);
        var found = dao.findBy("balance", 0.0);
        assertThat(found.size()).isEqualTo(5);
    }

    @Test
    void findByPaginated_withMultipleMatchingAccount_shouldReturnPage() {
        var list = List.of(
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("4000").balance(10).build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("6000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("7000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("8000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("9000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("10000").build());
        dao.persist(list);
        var page = dao.findBy("balance", 0.0, 2, 2);
        assertThat(page.total()).isEqualTo(5);
        assertThat(page.list().size()).isEqualTo(2);
        assertThat(page.list()).map(CheckingAccount::getAccountNumber).contains("8000", "9000");
    }

    @Test
    void countBy_withMultipleMatchingAccount_shouldReturnCount() {
        var list = List.of(
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("4000").balance(10).build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("6000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("7000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("8000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("9000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("10000").build());
        dao.persist(list);
        var count = dao.countBy("balance", 0.0);
        assertThat(count).isEqualTo(5);
    }

    @Test
    void findByUsername_withNoMatchingAccount_shouldReturnEmptyList() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10).build();
        dao.persist(account);
        var result = dao.findByAccountNumber(user.getUsername());
        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_withOneMatchingAccount_shouldReturnList() {
        var secondUser = User.builder()
                .username("test2")
                .email("test2@test.test")
                .password("test").build();
        dao.inTransaction(em -> em.persist(secondUser));
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10).build();
        var secondAccount = CheckingAccount.builder()
                .accountHolder(secondUser)
                .accountNumber("4000")
                .balance(10).build();
        dao.persist(account);
        dao.persist(secondAccount);
        var result = dao.findByUsername(user.getUsername());
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void removeBy_withNoAccount_shouldNotFail() {
        var removed = dao.removeBy("balance", 10.0);
        assertThat(removed).isEqualTo(0);
    }

    @Test
    void removeBy_withOneMatchingAccount_shouldRemove() {
        var account = CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0)
                .overdraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        var removed = dao.removeBy("balance", account.getBalance());
        var found = dao.find(account.getId());
        assertThat(removed).isEqualTo(1);
        assertThat(found).isEmpty();
    }

    @Test
    void removeBy_withMultipleMatchingAccount_shouldRemove() {
        var list = List.of(
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("4000").balance(10).build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("6000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("7000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("8000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("9000").build(),
                CheckingAccount.builder()
                        .accountHolder(user).accountNumber("10000").build());
        dao.persist(list);
        var removed = dao.removeBy("balance", 0.0);
        var found = dao.getAll();
        assertThat(removed).isEqualTo(5);
        assertThat(found.size()).isEqualTo(1);
    }

    @Test
    void sumBalanceHigherThan_withNoAccountHigher_shouldReturnSum() {
        var list = List.of(
                CheckingAccount.builder().accountNumber("4000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("6000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("7000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("8000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("9000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("10000")
                        .accountHolder(user).balance(100).build());
        dao.persist(list);
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(0.0);
    }

    @Test
    void sumBalanceHigherThan_withOneAccountHigher_shouldReturnSum() {
        var list = List.of(
                CheckingAccount.builder().accountNumber("4000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("6000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("7000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("8000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("9000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("10000")
                        .accountHolder(user).balance(2000).build());
        dao.persist(list);
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(2000.0);
    }

    @Test
    void sumBalanceHigherThan_withMultipleAccountHigher_shouldReturnSum() {
        var list = List.of(
                CheckingAccount.builder().accountNumber("4000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("6000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("7000")
                        .accountHolder(user).balance(600).build(),
                CheckingAccount.builder().accountNumber("8000")
                        .accountHolder(user).balance(3000).build(),
                CheckingAccount.builder().accountNumber("9000")
                        .accountHolder(user).balance(100).build(),
                CheckingAccount.builder().accountNumber("10000")
                        .accountHolder(user).balance(2000).build());
        dao.persist(list);
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(5600.0);
    }

    @Test
    void inTransaction_withException_shouldRollBack() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .accountHolder(user)
                .balance(100)
                .build();
        assertThatThrownBy(() -> {
            dao.inTransaction(em -> {
                em.persist(account);
                throw new RuntimeException();
            });
        }).isInstanceOf(RuntimeException.class);
        assertThat(dao.countAll()).isZero();
    }

    private void assertCheckingAccountEqual(CheckingAccount result, CheckingAccount expected) {
        assertThat(result.getAccountNumber()).isEqualTo(expected.getAccountNumber());
        assertThat(result.getBalance()).isEqualTo(expected.getBalance());
        assertThat(result.getOverdraftLimit()).isEqualTo(expected.getOverdraftLimit());
        assertThat(result.getDebt()).isEqualTo(expected.getDebt());
    }
}
