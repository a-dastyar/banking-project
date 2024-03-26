package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractDatabaseIT;
import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.model.User;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SavingAccountDAOIT extends AbstractDatabaseIT {

    private SavingAccountDAO dao;

    private EntityManager em;

    private User user;

    @BeforeEach
    void setup() {
        log.debug("setup");
        em = super.emf.createEntityManager();
        dao = new SavingAccountDAOImpl(em);
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
        var account = SavingAccount.builder()
                .accountHolder(user)
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        assertThatThrownBy(() -> dao.persist(account))
                .hasMessageContaining("null");
    }

    @Test
    void persist_withValidAccount_shouldSave() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertSavingAccountEqual(found, account);
    }

    @Test
    void persistList_withNullAccountNumber_shouldFail() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        assertThatThrownBy(() -> dao.persist(List.of(account)))
                .hasMessageContaining("null");
    }

    @Test
    void persistList_withValidAccount_shouldSave() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        dao.persist(List.of(account));
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertSavingAccountEqual(found, account);
    }

    @Test
    void persistList_withMultipleAccount_shouldSave() {
        var list = List.of(
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("5000")
                        .interestPeriod(InterestPeriod.YEARLY).interestRate(10.0)
                        .balance(10.0).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("6000")
                        .interestPeriod(InterestPeriod.YEARLY).interestRate(10.0).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("7000")
                        .interestPeriod(InterestPeriod.YEARLY).interestRate(10.0).build());
        dao.persist(list);
        var found = dao.findBy("balance", 0.0);
        assertThat(found.size()).isEqualTo(2);
    }

    @Test
    void find_withNoAccount_shouldReturnEmpty() {
        var found = dao.find(1L);
        assertThat(found).isEmpty();
    }

    @Test
    void find_withAccountId_shouldReturnAccount() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertSavingAccountEqual(found, account);
    }

    @Test
    void transactionalRemove_withAccount_shouldRemove() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
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
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("5000")
                        .interestPeriod(InterestPeriod.YEARLY).interestRate(10.0).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("6000")
                        .interestPeriod(InterestPeriod.YEARLY).interestRate(10.0).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("7000")
                        .interestPeriod(InterestPeriod.YEARLY).interestRate(10.0).build());
        dao.persist(list);
        var found = dao.getAll();
        assertThat(found.size()).isEqualTo(3);
    }

    @Test
    void getAllPaginated_withMultipleAccounts_shouldReturnPage() {
        var list = List.of(
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("5000")
                        .interestPeriod(InterestPeriod.YEARLY).interestRate(10.0).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("6000")
                        .interestPeriod(InterestPeriod.YEARLY).interestRate(10.0).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("7000")
                        .interestPeriod(InterestPeriod.YEARLY).interestRate(10.0).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("8000")
                        .interestPeriod(InterestPeriod.YEARLY).interestRate(10.0).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("9000")
                        .interestPeriod(InterestPeriod.YEARLY).interestRate(10.0).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("10000")
                        .interestPeriod(InterestPeriod.YEARLY).interestRate(10.0).build());
        dao.persist(list);
        var found = dao.getAll(2, 2);
        assertThat(found.total()).isEqualTo(6);
        assertThat(found.list().size()).isEqualTo(2);
        assertThat(found.list()).map(SavingAccount::getAccountNumber).contains("7000", "8000");
    }

    @Test
    void countAll_withMultipleAccounts_shouldReturnCount() {
        var list = List.of(
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("5000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("6000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("7000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("8000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("9000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("10000")
                        .interestPeriod(InterestPeriod.YEARLY).build());
        dao.persist(list);
        var count = dao.countAll();
        assertThat(count).isEqualTo(6);
    }

    @Test
    void exists_withSameAccount_shouldReturnTrue() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        dao.persist(account);
        var exists = dao.exists(account);
        assertThat(exists).isTrue();
    }

    @Test
    void exists_withSameAccountNumber_shouldReturnTrue() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        var newAccount = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        dao.persist(account);
        var exists = dao.exists(newAccount);
        assertThat(exists).isTrue();
    }

    @Test
    void exists_withDifferentAccountNumber_shouldReturnTrue() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        var newAccount = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("6000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        dao.persist(account);
        var exists = dao.exists(newAccount);
        assertThat(exists).isFalse();
    }

    @Test
    void update_withAccount_shouldUpdate() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        dao.persist(account);
        account.setBalance(30.0);
        dao.update(account);
        var found = dao.find(account.getId()).get();
        assertThat(found.getBalance()).isEqualTo(30.0);
    }

    @Test
    void findByAccountNumber_withNoAccount_shouldReturnEmpty() {
        var found = dao.findByAccountNumber("3000");
        assertThat(found).isEmpty();
    }

    @Test
    void findByAccountNumber_withAccountNumber_shouldReturnAccount() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();
        assertSavingAccountEqual(found, account);
    }

    @Test
    void findBy_withNoAccount_shouldReturnEmpty() {
        var found = dao.findBy("balance", 0.0);
        assertThat(found).isEmpty();
    }

    @Test
    void findBy_withOneMatchingAccount_shouldReturnAccounts() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        dao.persist(account);
        var found = dao.findBy("balance", account.getBalance());
        assertThat(found).isNotEmpty();
    }

    @Test
    void findBy_withMultipleMatchingAccount_shouldReturnAccounts() {
        var list = List.of(
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("5000")
                        .interestPeriod(InterestPeriod.YEARLY).balance(10.0).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("6000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("7000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("8000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("9000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("10000")
                        .interestPeriod(InterestPeriod.YEARLY).build());
        dao.persist(list);
        var found = dao.findBy("balance", 0.0);
        assertThat(found.size()).isEqualTo(5);
    }

    @Test
    void findByPaginated_withMultipleMatchingAccount_shouldReturnPage() {
        var list = List.of(
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("5000")
                        .interestPeriod(InterestPeriod.YEARLY).balance(10.0).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("6000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("7000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("8000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("9000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("10000")
                        .interestPeriod(InterestPeriod.YEARLY).build());
        dao.persist(list);
        var page = dao.findBy("balance", 0.0, 2, 2);
        assertThat(page.total()).isEqualTo(5);
        assertThat(page.list().size()).isEqualTo(2);
        assertThat(page.list()).map(SavingAccount::getAccountNumber).contains("8000", "9000");
    }

    @Test
    void countBy_withMultipleMatchingAccount_shouldReturnCount() {
        var list = List.of(
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("5000")
                        .interestPeriod(InterestPeriod.YEARLY).balance(10.0).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("6000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("7000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("8000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("9000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("10000")
                        .interestPeriod(InterestPeriod.YEARLY).build());
        dao.persist(list);
        var count = dao.countBy("balance", 0.0);
        assertThat(count).isEqualTo(5);
    }

    @Test
    void findByUsername_withNoMatchingAccount_shouldReturnEmptyList() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .interestPeriod(InterestPeriod.YEARLY)
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
        var account = SavingAccount.builder()
                .interestPeriod(InterestPeriod.YEARLY)
                .accountHolder(user)
                .accountNumber("3000")
                .balance(10).build();
        var secondAccount = SavingAccount.builder()
                .accountHolder(secondUser)
                .interestPeriod(InterestPeriod.YEARLY)
                .accountNumber("4000")
                .balance(10).build();
        dao.persist(account);
        dao.persist(secondAccount);
        var result = dao.findByUsername(user.getUsername(), 1, 50);
        assertThat(result.list().size()).isEqualTo(1);
    }

    @Test
    void removeBy_withNoAccount_shouldNotFail() {
        var removed = dao.removeBy("balance", 0.0);
        assertThat(removed).isEqualTo(0);
    }

    @Test
    void removeBy_withOneMatchingAccount_shouldRemove() {
        var account = SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("5000")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        dao.persist(account);
        var removed = dao.removeBy("balance", account.getBalance());
        var found = dao.find(account.getId());
        assertThat(removed).isEqualTo(1);
        assertThat(found).isEmpty();
    }

    @Test
    void removeBy_withMultipleMatchingAccount_shouldRemove() {
        var list = List.of(
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("5000").balance(10.0)
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("6000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("7000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("8000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("9000")
                        .interestPeriod(InterestPeriod.YEARLY).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("10000")
                        .interestPeriod(InterestPeriod.YEARLY).build());
        dao.persist(list);
        var removed = dao.removeBy("balance", 0);
        var found = dao.getAll();
        assertThat(removed).isEqualTo(5);
        assertThat(found.size()).isEqualTo(1);
    }

    @Test
    void sumBalanceHigherThan_withNoAccountHigher_shouldReturnSum() {
        var list = List.of(
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("5000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("6000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("7000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("8000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("9000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("10000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build());
        dao.persist(list);
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(0.0);
    }

    @Test
    void sumBalanceHigherThan_withOneAccountHigher_shouldReturnSum() {
        var list = List.of(
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("5000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("6000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("7000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("8000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("9000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("10000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(2000).build());
        dao.persist(list);
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(2000.0);
    }

    @Test
    void sumBalanceHigherThan_withMultipleAccountHigher_shouldReturnSum() {
        var list = List.of(
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("5000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(5000).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("6000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(900).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("7000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(500).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("8000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("9000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100).build(),
                SavingAccount.builder()
                        .accountHolder(user).accountNumber("10000")
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(2000).build());
        dao.persist(list);
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(7900.0);
    }

    @Test
    void applyInterest_withMultipleAccountHigher_shouldAddInterestToBalance() {
        var list = List.of(
                SavingAccount.builder().accountNumber("5000")
                        .accountHolder(user)
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(5000)
                        .interestRate(10.0).build(),
                SavingAccount.builder().accountNumber("8000")
                        .accountHolder(user)
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100)
                        .interestRate(20.0).build(),
                SavingAccount.builder().accountNumber("10000")
                        .accountHolder(user)
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(2000)
                        .interestRate(0.0).build());
        dao.persist(list);
        dao.applyInterest();
        var sum = dao.sumBalanceHigherThan(0);
        assertThat(sum).isEqualTo(7620.0);
    }

    @Test
    void inTransaction_withException_shouldRollBack() {
        var account = SavingAccount.builder().accountNumber("5000")
                .accountHolder(user)
                .interestPeriod(InterestPeriod.YEARLY)
                .balance(5000)
                .interestRate(10.0).build();
        assertThatThrownBy(() -> {
            dao.inTransaction(em -> {
                em.persist(account);
                throw new RuntimeException();
            });
        }).isInstanceOf(RuntimeException.class);
        assertThat(dao.countAll()).isZero();
    }

    private void assertSavingAccountEqual(SavingAccount result, SavingAccount expected) {
        assertThat(result.getAccountNumber()).isEqualTo(expected.getAccountNumber());
        assertThat(result.getBalance()).isEqualTo(expected.getBalance());
        assertThat(result.getMinimumBalance()).isEqualTo(expected.getMinimumBalance());
        assertThat(result.getInterestRate()).isEqualTo(expected.getInterestRate());
        assertThat(result.getInterestPeriod()).isEqualTo(expected.getInterestPeriod());
    }
}
