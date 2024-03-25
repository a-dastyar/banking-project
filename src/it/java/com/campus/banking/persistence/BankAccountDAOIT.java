package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractDatabaseIT;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.User;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BankAccountDAOIT extends AbstractDatabaseIT {

    private BankAccountDAO<BankAccount> dao;

    private EntityManager em;

    private User user;

    @BeforeEach
    void setup() {
        log.debug("setup");
        em = super.emf.createEntityManager();
        dao = new BankAccountDAOImpl(em);
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
    void persist_withValidAccount_shouldSave() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertBankAccountEqual(found, account);
    }

    @Test
    void persistList_withNullAccountNumber_shouldFail() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .balance(10.0).build();
        assertThatThrownBy(() -> dao.persist(List.of(account)))
                .hasMessageContaining("null");
    }

    @Test
    void persistList_withValidAccount_shouldSave() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(List.of(account));
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertBankAccountEqual(found, account);
    }

    @Test
    void persistList_withMultipleAccount_shouldSave() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(List.of(account, account.withAccountNumber("4000")));
        assertThat(account.getId()).isNotNull();
        var found = dao.getAll();
        assertThat(found.size()).isEqualTo(2);
    }

    @Test
    void find_withNoAccount_shouldReturnEmpty() {
        var found = dao.find(1L);
        assertThat(found).isEmpty();
    }

    @Test
    void find_withAccountId_shouldReturnAccount() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertBankAccountEqual(found, account);
    }

    @Test
    void transactionalRemove_withAccount_shouldRemove() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
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
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        var list = List.of(account, account.withAccountNumber("4000"));
        dao.persist(list);
        var found = dao.getAll();
        assertThat(found.size()).isEqualTo(2);
    }

    @Test
    void getAllPaginated_withMultipleAccounts_shouldReturnPage() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        var list = List.of(
                account,
                account.withAccountNumber("4000"),
                account.withAccountNumber("5000"),
                account.withAccountNumber("6000"),
                account.withAccountNumber("7000"),
                account.withAccountNumber("8000"));
        dao.persist(list);
        var found = dao.getAll(2, 2);
        assertThat(found.total()).isEqualTo(6);
        assertThat(found.list().size()).isEqualTo(2);
        assertThat(found.list()).map(BankAccount::getAccountNumber).contains("5000", "6000");
    }

    @Test
    void countAll_withMultipleAccounts_shouldReturnCount() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        var list = List.of(
                account,
                account.withAccountNumber("4000"),
                account.withAccountNumber("5000"),
                account.withAccountNumber("6000"),
                account.withAccountNumber("7000"),
                account.withAccountNumber("8000"));
        dao.persist(list);
        var count = dao.countAll();
        assertThat(count).isEqualTo(6);
    }

    @Test
    void exists_withSameAccount_shouldReturnTrue() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(account);
        var exists = dao.exists(account);
        assertThat(exists).isTrue();
    }

    @Test
    void exists_withSameAccountNumber_shouldReturnTrue() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        var newAccount = BankAccount.builder()
                .accountNumber("3000")
                .balance(130.0).build();
        dao.persist(account);
        var exists = dao.exists(newAccount);
        assertThat(exists).isTrue();
    }

    @Test
    void exists_withDifferentAccountNumber_shouldReturnTrue() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        var newAccount = BankAccount.builder()
                .accountNumber("4000")
                .balance(130.0).build();
        dao.persist(account);
        var exists = dao.exists(newAccount);
        assertThat(exists).isFalse();
    }

    @Test
    void update_withAccount_shouldUpdate() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(account);
        dao.update(account.withBalance(30.0));
        var found = dao.find(account.getId()).get();
        assertThat(found.getBalance()).isEqualTo(30.0);
    }

    @Test
    void updateList_withAccount_shouldUpdate() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        var secondAccount = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("4000")
                .balance(10.0).build();
        dao.persist(List.of(account, secondAccount));
        dao.update(List.of(account.withBalance(30.0), secondAccount.withBalance(40)));
        var sum = dao.sumBalanceHigherThan(0);
        assertThat(sum).isEqualTo(70.0);
    }

    @Test
    void findByAccountNumber_withNoAccount_shouldReturnEmpty() {
        var found = dao.findByAccountNumber("3000");
        assertThat(found).isEmpty();
    }

    @Test
    void findByAccountNumber_withAccountNumber_shouldReturnAccount() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();
        assertBankAccountEqual(found, account);
    }

    @Test
    void findBy_withNoAccount_shouldReturnEmpty() {
        var found = dao.findBy("balance", 10.0);
        assertThat(found).isEmpty();
    }

    @Test
    void findBy_withOneMatchingAccount_shouldReturnAccounts() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(account);
        var found = dao.findBy("balance", account.getBalance());
        assertThat(found).isNotEmpty();
    }

    @Test
    void findBy_withMultipleMatchingAccount_shouldReturnAccounts() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(List.of(
                account.withBalance(0),
                account.withAccountNumber("4000"),
                account.withAccountNumber("5000")));
        var found = dao.findBy("balance", 10.0);
        assertThat(found.size()).isEqualTo(2);
    }

    @Test
    void findByPaginated_withMultipleMatchingAccount_shouldReturnPage() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(List.of(
                account.withBalance(0),
                account.withAccountNumber("4000"),
                account.withAccountNumber("5000"),
                account.withAccountNumber("6000")));
        var page = dao.findBy("balance", 10.0, 2, 2);
        assertThat(page.total()).isEqualTo(3);
        assertThat(page.list().size()).isEqualTo(1);
        assertThat(page.list()).map(BankAccount::getAccountNumber).contains("6000");
    }

    @Test
    void countBy_withMultipleMatchingAccount_shouldReturnCount() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(List.of(
                account.withBalance(0),
                account.withAccountNumber("4000"),
                account.withAccountNumber("5000"),
                account.withAccountNumber("6000")));
        var count = dao.countBy("balance", 10.0);
        assertThat(count).isEqualTo(3);
    }

    @Test
    void findByUsername_withNoMatchingAccount_shouldReturnEmptyList() {
        dao.inTransaction(em -> em.persist(user));
        var account = BankAccount.builder()
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
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10).build();
        var secondAccount = BankAccount.builder()
                .accountHolder(secondUser)
                .accountNumber("4000")
                .balance(10).build();
        dao.persist(account);
        dao.persist(secondAccount);
        var result = dao.findByUsername(user.getUsername());
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void removeBy_withInvalidField_shouldNotFail() {
        assertThatThrownBy(() -> dao.removeBy("invalid", 10.0))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void removeBy_withNoAccount_shouldNotFail() {
        var removed = dao.removeBy("balance", 10.0);
        assertThat(removed).isEqualTo(0);
    }

    @Test
    void removeBy_withOneMatchingAccount_shouldRemove() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(account);
        var removed = dao.removeBy("balance", 10.0);
        var found = dao.find(account.getId());
        assertThat(removed).isEqualTo(1);
        assertThat(found).isEmpty();
    }

    @Test
    void removeBy_withMultipleMatchingAccount_shouldRemove() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(List.of(
                account.withBalance(0),
                account.withAccountNumber("4000"),
                account.withAccountNumber("5000")));
        var removed = dao.removeBy("balance", 10);
        var found = dao.getAll();
        assertThat(removed).isEqualTo(2);
        assertThat(found.size()).isEqualTo(1);
    }

    @Test
    void sumBalanceHigherThan_withNoAccountHigher_shouldReturnSum() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(List.of(
                account,
                account.withAccountNumber("4000"),
                account.withAccountNumber("5000")));
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(0.0);
    }

    @Test
    void sumBalanceHigherThan_withOneAccountHigher_shouldReturnSum() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(List.of(
                account,
                account.withAccountNumber("4000"),
                account.withAccountNumber("5000")
                        .withBalance(2000.0)));
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(2000.0);
    }

    @Test
    void sumBalanceHigherThan_withMultipleAccountHigher_shouldReturnSum() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        dao.persist(List.of(
                account,
                account.withAccountNumber("4000")
                        .withBalance(1000.0),
                account.withAccountNumber("5000")
                        .withBalance(2000.0)));
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(3000.0);
    }

    @Test
    void inTransaction_withException_shouldRollBack() {
        var account = BankAccount.builder()
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10.0).build();
        assertThatThrownBy(() -> {
            dao.inTransaction(em -> {
                em.persist(account);
                throw new RuntimeException();
            });
        }).isInstanceOf(RuntimeException.class);
        assertThat(dao.countAll()).isZero();
    }

    private void assertBankAccountEqual(BankAccount result, BankAccount expected) {
        assertThat(result.getAccountNumber()).isEqualTo(expected.getAccountNumber());
        assertThat(result.getBalance()).isEqualTo(expected.getBalance());
    }
}
