package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
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
public abstract class AbstractAccountDAOIT<T extends BankAccount> extends AbstractDatabaseIT {

    protected BankAccountDAO<T> dao;

    protected EntityManager em;

    protected User user;

    @BeforeEach
    void createEMF() {
        log.debug("setup");
        em = super.emf.createEntityManager();
    }

    @AfterEach
    void destroyEMF() {
        log.debug("teardown");
        em.close();
    }

    @Test
    void persist_withValidAccount_shouldSave() {
        var account = generateAccounts().findFirst().get();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertAccountsEqual(found, account);
    }

    @Test
    void persistList_withNullAccountNumber_shouldFail() {
        List<T> accounts = generateAccounts()
                .peek(t -> t.setAccountNumber(null))
                .limit(1)
                .toList();
        assertThatThrownBy(() -> dao.persist(accounts))
                .hasMessageContaining("null");
    }

    @Test
    void persistList_withValidAccount_shouldSave() {
        var account = generateAccounts().findFirst().get();
        dao.persist(List.of(account));
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertAccountsEqual(found, account);
    }

    @Test
    void persistList_withMultipleAccount_shouldSave() {
        var accounts = generateAccounts()
                .limit(5)
                .toList();
        dao.persist(accounts);
        assertThat(accounts.getLast().getId()).isNotNull();
        var found = dao.getAll();
        assertThat(found.size()).isEqualTo(5);
    }

    @Test
    void find_withNoAccount_shouldReturnEmpty() {
        var found = dao.find(1L);
        assertThat(found).isEmpty();
    }

    @Test
    void find_withAccountId_shouldReturnAccount() {
        var account = generateAccounts().findFirst().get();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertAccountsEqual(found, account);
    }

    @Test
    void transactionalRemove_withAccount_shouldRemove() {
        var account = generateAccounts().findFirst().get();
        dao.persist(account);
        dao.inTransaction(em -> {
            var found = dao.findByAccountNumberForUpdate(em,
                    account.getAccountNumber()).get();
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
        var accounts = generateAccounts()
                .limit(5)
                .toList();
        dao.persist(accounts);
        var found = dao.getAll();
        assertThat(found.size()).isEqualTo(5);
    }

    @Test
    void getAllPaginated_withMultipleAccounts_shouldReturnPage() {
        var accounts = generateAccounts()
                .limit(30)
                .toList();
        dao.persist(accounts);
        var found = dao.getAll(2, 10);
        assertThat(found.total()).isEqualTo(30);
        assertThat(found.list().size()).isEqualTo(10);
    }

    @Test
    void getAllOrdered_withOrderByBalanceASC_shouldReturnPage() {
        var accounts = generateAccounts()
                .limit(30)
                .toList();
        dao.persist(accounts);
        var found = dao.getAllOrdered(1, 10,"balance",Order.ASC);
        assertThat(found.total()).isEqualTo(30);
        assertThat(found.list().size()).isEqualTo(10);
        assertAccountsEqual(found.list().getFirst(), accounts.getFirst());
    }

    @Test
    void getAllOrdered_withOrderByBalanceDESC_shouldReturnPage() {
        var accounts = generateAccounts()
                .limit(30)
                .toList();
        dao.persist(accounts);
        var found = dao.getAllOrdered(1, 10,"balance",Order.DESC);
        assertThat(found.total()).isEqualTo(30);
        assertThat(found.list().size()).isEqualTo(10);
        assertAccountsEqual(found.list().getFirst(), accounts.getLast());
    }

    @Test
    void countAll_withMultipleAccounts_shouldReturnCount() {
        var accounts = generateAccounts()
                .limit(5)
                .toList();
        dao.persist(accounts);
        var count = dao.countAll();
        assertThat(count).isEqualTo(5);
    }

    @Test
    void exists_withSameAccount_shouldReturnTrue() {
        var account = generateAccounts().findFirst().get();
        dao.persist(account);
        var exists = dao.exists(account);
        assertThat(exists).isTrue();
    }

    @Test
    void exists_withSameAccountNumber_shouldReturnTrue() {
        var account = generateAccounts().findFirst().get();
        var newAccount = generateAccounts()
                .peek(acc -> acc.setAccountNumber(account.getAccountNumber()))
                .findFirst().get();
        dao.persist(account);
        var exists = dao.exists(newAccount);
        assertThat(exists).isTrue();
    }

    @Test
    void exists_withDifferentAccountNumber_shouldReturnTrue() {
        var accounts = generateAccounts()
                .limit(2)
                .toList();
        dao.persist(accounts.getFirst());
        var exists = dao.exists(accounts.getLast());
        assertThat(exists).isFalse();
    }

    @Test
    void update_withAccount_shouldUpdate() {
        var account = generateAccounts().findFirst().get();
        dao.persist(account);
        account.setBalance(30.0);
        dao.update(account);
        var found = dao.find(account.getId()).get();
        assertThat(found.getBalance()).isEqualTo(30.0);
    }

    @Test
    void updateList_withAccount_shouldUpdate() {
        var accounts = generateAccounts()
                .limit(5)
                .toList();
        dao.persist(accounts);
        accounts.forEach(acc -> acc.setBalance(12));
        dao.update(accounts);
        var sum = dao.sumBalanceHigherThan(0);
        assertThat(sum).isEqualTo(5 * 12);
    }

    @Test
    void findByAccountNumber_withNoAccount_shouldReturnEmpty() {
        var found = dao.findByAccountNumber("3000");
        assertThat(found).isEmpty();
    }

    @Test
    void findByAccountNumber_withAccountNumber_shouldReturnAccount() {
        var account = generateAccounts().findFirst().get();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();
        assertAccountsEqual(found, account);
    }

    @Test
    void findBy_withNoAccount_shouldReturnEmpty() {
        var found = dao.findBy("balance", 10.0);
        assertThat(found).isEmpty();
    }

    @Test
    void findBy_withOneMatchingAccount_shouldReturnAccounts() {
        var account = generateAccounts().findFirst().get();
        dao.persist(account);
        var found = dao.findBy("balance", account.getBalance());
        assertThat(found).isNotEmpty();
    }

    @Test
    void findBy_withMultipleMatchingAccount_shouldReturnAccounts() {
        var accounts = generateAccounts()
                .peek(acc -> acc.setBalance(10.0))
                .limit(5)
                .toList();
        accounts.getLast().setBalance(15.0);
        dao.persist(accounts);
        var found = dao.findBy("balance", 10.0);
        assertThat(found.size()).isEqualTo(4);
    }

    @Test
    void findByPaginated_withMultipleMatchingAccount_shouldReturnPage() {
        var accounts = generateAccounts()
                .peek(acc -> acc.setBalance(10.0))
                .limit(5)
                .toList();
        accounts.getLast().setBalance(15.0);
        dao.persist(accounts);
        var page = dao.findBy("balance", 10.0, 2, 2);
        assertThat(page.total()).isEqualTo(4);
        assertThat(page.list().size()).isEqualTo(2);
    }

    @Test
    void findByOrdered_withOrderByAccountNumberASC_shouldReturnPage() {
        var accounts = generateAccounts()
                .peek(acc -> acc.setBalance(10.0))
                .limit(5)
                .toList();
        accounts.getLast().setBalance(15.0);
        dao.persist(accounts);
        var page = dao.findByOrdered("balance", 10.0, 1, 2,"accountNumber",Order.ASC);
        assertThat(page.total()).isEqualTo(4);
        assertThat(page.list().size()).isEqualTo(2);
        assertAccountsEqual(page.list().getFirst(),accounts.getFirst());
    }

    @Test
    void findByOrdered_withOrderByAccountNumberDESC_shouldReturnPage() {
        var accounts = generateAccounts()
                .peek(acc -> acc.setBalance(10.0))
                .limit(5)
                .toList();
        accounts.getLast().setBalance(15.0);
        dao.persist(accounts);
        var page = dao.findByOrdered("balance", 10.0, 1, 2,"accountNumber",Order.DESC);
        assertThat(page.total()).isEqualTo(4);
        assertThat(page.list().size()).isEqualTo(2);
        assertAccountsEqual(page.list().getFirst(),accounts.get(3));
    }

    @Test
    void countBy_withMultipleMatchingAccount_shouldReturnCount() {
        var accounts = generateAccounts()
                .peek(acc -> acc.setBalance(10.0))
                .limit(5)
                .toList();
        accounts.getLast().setBalance(15.0);
        dao.persist(accounts);
        var count = dao.countBy("balance", 10.0);
        assertThat(count).isEqualTo(4);
    }

    @Test
    void findByUsername_withNoMatchingAccount_shouldReturnEmptyList() {
        var accounts = generateAccounts()
                .peek(acc -> acc.setBalance(10.0))
                .limit(5)
                .toList();
        dao.persist(accounts);
        var result = dao.findByAccountNumber("no match");
        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_withOneMatchingAccount_shouldReturnList() {
        var secondUser = User.builder()
                .username("test2")
                .email("test2@test.test")
                .password("test").build();
        dao.inTransaction(em -> em.persist(secondUser));
        var accounts = generateAccounts()
                .peek(acc -> acc.setBalance(10.0))
                .limit(5)
                .toList();
        accounts.getFirst().setAccountHolder(secondUser);
        dao.persist(accounts);
        var result = dao.findByUsername(secondUser.getUsername(), 1, 50);
        assertThat(result.total()).isEqualTo(1);
        assertThat(result.list().size()).isEqualTo(1);
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
        var accounts = generateAccounts()
                .limit(5)
                .toList();
        dao.persist(accounts);
        var removed = dao.removeBy("balance", accounts.getFirst().getBalance());
        var found = dao.find(accounts.getFirst().getId());
        assertThat(removed).isEqualTo(1);
        assertThat(found).isEmpty();
    }

    @Test
    void removeBy_withMultipleMatchingAccount_shouldRemove() {
        var accounts = generateAccounts()
                .peek(acc -> acc.setBalance(10.0))
                .limit(5)
                .toList();
        accounts.getFirst().setBalance(15);
        dao.persist(accounts);
        var removed = dao.removeBy("balance", 10);
        var found = dao.getAll();
        assertThat(removed).isEqualTo(4);
        assertThat(found.size()).isEqualTo(1);
    }

    @Test
    void sumBalanceHigherThan_withNoAccountHigher_shouldReturnSum() {
        var accounts = generateAccounts()
                .peek(acc -> acc.setBalance(10.0))
                .limit(5)
                .toList();
        dao.persist(accounts);
        var sum = dao.sumBalanceHigherThan(15);
        assertThat(sum).isEqualTo(0.0);
    }

    @Test
    void sumBalanceHigherThan_withOneAccountHigher_shouldReturnSum() {
        var accounts = generateAccounts()
                .peek(acc -> acc.setBalance(10.0))
                .limit(5)
                .toList();
        accounts.getFirst().setBalance(16);
        dao.persist(accounts);
        var sum = dao.sumBalanceHigherThan(15);
        assertThat(sum).isEqualTo(16.0);
    }

    @Test
    void sumBalanceHigherThan_withMultipleAccountHigher_shouldReturnSum() {
        var accounts = generateAccounts()
                .peek(acc -> acc.setBalance(20.0))
                .limit(5)
                .toList();
        accounts.getFirst().setBalance(10);
        dao.persist(accounts);
        var sum = dao.sumBalanceHigherThan(15);
        assertThat(sum).isEqualTo(80.0);
    }

    @Test
    void inTransaction_withException_shouldRollBack() {
        var account = generateAccounts().findFirst().get();
        assertThatThrownBy(() -> {
            dao.inTransaction(em -> {
                em.persist(account);
                throw new RuntimeException();
            });
        }).isInstanceOf(RuntimeException.class);
        assertThat(dao.countAll()).isZero();
    }

    protected abstract Stream<T> generateAccounts();

    protected abstract void assertAccountsEqual(T first, T second);

}
