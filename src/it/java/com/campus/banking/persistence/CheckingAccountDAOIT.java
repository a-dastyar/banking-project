package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractIT;
import com.campus.banking.model.CheckingAccount;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckingAccountDAOIT extends AbstractIT {

    BankAccountDAO<CheckingAccount> dao;

    @BeforeEach
    void setup() {
        log.debug("setup");
        dao = new CheckingAccountDAOImpl(super.db);
    }

    @AfterEach
    void teardown() {
        log.debug("teardown");
    }

    @Test
    void persist_withNullAccountNumber_shouldFail() {
        var account = CheckingAccount.builder()
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        assertThatThrownBy(() -> dao.persist(account))
                .hasMessageContaining("null");
    }

    @Test
    void persist_withNullAccountHolderName_shouldFail() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        assertThatThrownBy(() -> dao.persist(account))
                .hasMessageContaining("null");
    }

    @Test
    void persist_withValidAccount_shouldSave() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertCheckingAccountEqual(found, account);
    }

    @Test
    void persistList_withNullAccountNumber_shouldFail() {
        var account = CheckingAccount.builder()
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        assertThatThrownBy(() -> dao.persist(List.of(account)))
                .hasMessageContaining("null");
    }

    @Test
    void persistList_withNullAccountHolderName_shouldFail() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        assertThatThrownBy(() -> dao.persist(List.of(account)))
                .hasMessageContaining("null");
    }

    @Test
    void persistList_withValidAccount_shouldSave() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(List.of(account));
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertCheckingAccountEqual(found, account);
    }

    @Test
    void persistList_withMultipleAccount_shouldSave() {
        var list = List.of(
                CheckingAccount.builder().accountNumber("4000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("5000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("6000")
                        .accountHolderName("New Tester").build());
        dao.persist(list);
        var found = dao.findBy("accountHolderName", "Tester");
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
                .accountNumber("4000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertCheckingAccountEqual(found, account);
    }

    @Test
    void transactionalRemove_withAccount_shouldRemove() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
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
                CheckingAccount.builder().accountNumber("4000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("5000")
                        .accountHolderName("Tester").build());
        dao.persist(list);
        var found = dao.getAll();
        assertThat(found.size()).isEqualTo(2);
    }

    @Test
    void getAllPaginated_withMultipleAccounts_shouldReturnPage() {
        var list = List.of(
                CheckingAccount.builder().accountNumber("4000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("6000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("7000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("8000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("9000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("10000")
                        .accountHolderName("Tester").build());
        dao.persist(list);
        var found = dao.getAll(2, 2);
        assertThat(found.total()).isEqualTo(6);
        assertThat(found.list().size()).isEqualTo(2);
        assertThat(found.list()).map(CheckingAccount::getAccountNumber).contains("7000", "8000");
    }

    @Test
    void countAll_withMultipleAccounts_shouldReturnCount() {
        var list = List.of(
                CheckingAccount.builder().accountNumber("4000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("6000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("7000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("8000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("9000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("10000")
                        .accountHolderName("Tester").build());
        dao.persist(list);
        var count = dao.countAll();
        assertThat(count).isEqualTo(6);
    }

    @Test
    void exists_withSameAccount_shouldReturnTrue() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        var exists = dao.exists(account);
        assertThat(exists).isTrue();
    }

    @Test
    void exists_withSameAccountNumber_shouldReturnTrue() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        var newAccount = CheckingAccount.builder()
                .accountNumber("4000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        var exists = dao.exists(newAccount);
        assertThat(exists).isTrue();
    }

    @Test
    void exists_withDifferentAccountNumber_shouldReturnTrue() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        var newAccount = CheckingAccount.builder()
                .accountNumber("5000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        var exists = dao.exists(newAccount);
        assertThat(exists).isFalse();
    }

    @Test
    void update_withAccount_shouldUpdate() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        account.setAccountHolderName("Updated");
        dao.update(account);
        var found = dao.find(account.getId()).get();
        assertThat(found.getAccountHolderName()).isEqualTo("Updated");
    }

    @Test
    void findByAccountNumber_withNoAccount_shouldReturnEmpty() {
        var found = dao.findByAccountNumber("3000");
        assertThat(found).isEmpty();
    }

    @Test
    void findByAccountNumber_withAccountNumber_shouldReturnAccount() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();
        assertCheckingAccountEqual(found, account);
    }

    @Test
    void findBy_withNoAccount_shouldReturnEmpty() {
        var found = dao.findBy("accountHolderName", "Tester");
        assertThat(found).isEmpty();
    }

    @Test
    void findBy_withOneMatchingAccount_shouldReturnAccounts() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        var found = dao.findBy("accountHolderName", account.getAccountHolderName());
        assertThat(found).isNotEmpty();
    }

    @Test
    void findBy_withMultipleMatchingAccount_shouldReturnAccounts() {
        var list = List.of(
                CheckingAccount.builder().accountNumber("4000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("6000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("7000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("8000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("9000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("10000")
                        .accountHolderName("New Tester").build());
        dao.persist(list);
        var found = dao.findBy("accountHolderName", "Tester");
        assertThat(found.size()).isEqualTo(5);
    }

    @Test
    void removeBy_withNoAccount_shouldNotFail() {
        var removed = dao.removeBy("accountHolderName", "Tester");
        assertThat(removed).isEqualTo(0);
    }

    @Test
    void removeBy_withOneMatchingAccount_shouldRemove() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .accountHolderName("Tester")
                .balance(10.0)
                .overDraftLimit(100.0)
                .debt(0.0).build();
        dao.persist(account);
        var removed = dao.removeBy("accountHolderName", account.getAccountHolderName());
        var found = dao.find(account.getId());
        assertThat(removed).isEqualTo(1);
        assertThat(found).isEmpty();
    }

    @Test
    void removeBy_withMultipleMatchingAccount_shouldRemove() {
        var list = List.of(
                CheckingAccount.builder().accountNumber("4000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("6000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("7000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("8000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("9000")
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("10000")
                        .accountHolderName("New Tester").build());
        dao.persist(list);
        var removed = dao.removeBy("accountHolderName", "Tester");
        var found = dao.findBy("accountHolderName", "Tester");
        assertThat(removed).isEqualTo(5);
        assertThat(found).isEmpty();
    }

    @Test
    void sumBalanceHigherThan_withNoAccountHigher_shouldReturnSum() {
        var list = List.of(
                CheckingAccount.builder().accountNumber("4000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("6000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("7000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("8000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("9000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("10000")
                        .balance(100)
                        .accountHolderName("Tester").build());
        dao.persist(list);
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(0.0);
    }

    @Test
    void sumBalanceHigherThan_withOneAccountHigher_shouldReturnSum() {
        var list = List.of(
                CheckingAccount.builder().accountNumber("4000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("6000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("7000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("8000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("9000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("10000")
                        .balance(2000)
                        .accountHolderName("Tester").build());
        dao.persist(list);
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(2000.0);
    }

    @Test
    void sumBalanceHigherThan_withMultipleAccountHigher_shouldReturnSum() {
        var list = List.of(
                CheckingAccount.builder().accountNumber("4000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("6000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("7000")
                        .balance(600)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("8000")
                        .balance(3000)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("9000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                CheckingAccount.builder().accountNumber("10000")
                        .balance(2000)
                        .accountHolderName("Tester").build());
        dao.persist(list);
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(5600.0);
    }

    private void assertCheckingAccountEqual(CheckingAccount result, CheckingAccount expected) {
        assertThat(result.getAccountNumber()).isEqualTo(expected.getAccountNumber());
        assertThat(result.getAccountHolderName()).isEqualTo(expected.getAccountHolderName());
        assertThat(result.getBalance()).isEqualTo(expected.getBalance());
        assertThat(result.getOverDraftLimit()).isEqualTo(expected.getOverDraftLimit());
        assertThat(result.getDebt()).isEqualTo(expected.getDebt());
    }
}