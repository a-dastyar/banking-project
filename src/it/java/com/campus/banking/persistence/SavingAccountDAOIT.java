package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractIT;
import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SavingAccountDAOIT extends AbstractIT {

    SavingAccountDAO dao;

    @BeforeEach
    void setup() {
        log.debug("setup");
        dao = new SavingAccountDAOImpl(super.db);
    }

    @AfterEach
    void teardown() {
        log.debug("teardown");
    }

    @Test
    void persist_withNullAccountNumber_shouldFail() {
        var account = SavingAccount.builder()
                .accountHolderName("Tester")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        assertThatThrownBy(() -> dao.persist(account))
                .hasMessageContaining("null");
    }

    @Test
    void persist_withNullAccountHolderName_shouldFail() {
        var account = SavingAccount.builder()
                .accountNumber("5000")
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
                .accountNumber("5000")
                .accountHolderName("Tester")
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
                .accountHolderName("Tester")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        assertThatThrownBy(() -> dao.persist(List.of(account)))
                .hasMessageContaining("null");
    }

    @Test
    void persistList_withNullAccountHolderName_shouldFail() {
        var account = SavingAccount.builder()
                .accountNumber("5000")
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
                .accountNumber("5000")
                .accountHolderName("Tester")
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
                SavingAccount.builder().accountNumber("5000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("6000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("7000")
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
        var account = SavingAccount.builder()
                .accountNumber("5000")
                .accountHolderName("Tester")
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
                .accountNumber("5000")
                .accountHolderName("Tester")
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
                SavingAccount.builder().accountNumber("5000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("6000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("7000")
                        .accountHolderName("New Tester").build());
        dao.persist(list);
        var found = dao.getAll();
        assertThat(found.size()).isEqualTo(3);
    }

    @Test
    void getAllPaginated_withMultipleAccounts_shouldReturnPage() {
        var list = List.of(
                SavingAccount.builder().accountNumber("5000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("6000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("7000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("8000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("9000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("10000")
                        .accountHolderName("New Tester").build());
        dao.persist(list);
        var found = dao.getAll(2, 2);
        assertThat(found.total()).isEqualTo(6);
        assertThat(found.list().size()).isEqualTo(2);
        assertThat(found.list()).map(SavingAccount::getAccountNumber).contains("7000", "8000");
    }

    @Test
    void countAll_withMultipleAccounts_shouldReturnCount() {
        var list = List.of(
                SavingAccount.builder().accountNumber("5000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("6000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("7000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("8000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("9000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("10000")
                        .accountHolderName("New Tester").build());
        dao.persist(list);
        var count = dao.countAll();
        assertThat(count).isEqualTo(6);
    }

    @Test
    void exists_withSameAccount_shouldReturnTrue() {
        var account = SavingAccount.builder()
                .accountNumber("5000")
                .accountHolderName("Tester")
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
                .accountNumber("5000")
                .accountHolderName("Tester")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        var newAccount = SavingAccount.builder()
                .accountNumber("5000")
                .accountHolderName("Tester")
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
                .accountNumber("5000")
                .accountHolderName("Tester")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        var newAccount = SavingAccount.builder()
                .accountNumber("6000")
                .accountHolderName("Tester")
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
                .accountNumber("5000")
                .accountHolderName("Tester")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
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
        var account = SavingAccount.builder()
                .accountNumber("5000")
                .accountHolderName("Tester")
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
        var found = dao.findBy("accountHolderName", "Tester");
        assertThat(found).isEmpty();
    }

    @Test
    void findBy_withOneMatchingAccount_shouldReturnAccounts() {
        var account = SavingAccount.builder()
                .accountNumber("5000")
                .accountHolderName("Tester")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        dao.persist(account);
        var found = dao.findBy("accountHolderName", account.getAccountHolderName());
        assertThat(found).isNotEmpty();
    }

    @Test
    void findBy_withMultipleMatchingAccount_shouldReturnAccounts() {
        var list = List.of(
                SavingAccount.builder().accountNumber("5000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("6000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("7000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("8000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("9000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("10000")
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
        var account = SavingAccount.builder()
                .accountNumber("5000")
                .accountHolderName("Tester")
                .balance(10.0)
                .minimumBalance(100.0)
                .interestRate(0.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
        dao.persist(account);
        var removed = dao.removeBy("accountHolderName", account.getAccountHolderName());
        var found = dao.find(account.getId());
        assertThat(removed).isEqualTo(1);
        assertThat(found).isEmpty();
    }

    @Test
    void removeBy_withMultipleMatchingAccount_shouldRemove() {
        var list = List.of(
                SavingAccount.builder().accountNumber("5000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("6000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("7000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("8000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("9000")
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("10000")
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
                SavingAccount.builder().accountNumber("5000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("6000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("7000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("8000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("9000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("10000")
                        .balance(100)
                        .accountHolderName("New Tester").build());
        dao.persist(list);
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(0.0);
    }

    @Test
    void sumBalanceHigherThan_withOneAccountHigher_shouldReturnSum() {
        var list = List.of(
                SavingAccount.builder().accountNumber("5000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("6000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("7000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("8000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("9000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("10000")
                        .balance(2000)
                        .accountHolderName("New Tester").build());
        dao.persist(list);
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(2000.0);
    }

    @Test
    void sumBalanceHigherThan_withMultipleAccountHigher_shouldReturnSum() {
        var list = List.of(
                SavingAccount.builder().accountNumber("5000")
                        .balance(5000)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("6000")
                        .balance(900)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("7000")
                        .balance(500)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("8000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("9000")
                        .balance(100)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("10000")
                        .balance(2000)
                        .accountHolderName("New Tester").build());
        dao.persist(list);
        var sum = dao.sumBalanceHigherThan(500);
        assertThat(sum).isEqualTo(7900.0);
    }

    @Test
    void applyInterest_withMultipleAccountHigher_shouldAddInterestToBalance() {
        var list = List.of(
                SavingAccount.builder().accountNumber("5000")
                        .balance(5000)
                        .interestRate(10.0)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("8000")
                        .balance(100)
                        .interestRate(20.0)
                        .accountHolderName("Tester").build(),
                SavingAccount.builder().accountNumber("10000")
                        .balance(2000)
                        .interestRate(0.0)
                        .accountHolderName("New Tester").build());
        dao.persist(list);
        dao.applyInterest();
        var sum = dao.sumBalanceHigherThan(0);
        assertThat(sum).isEqualTo(7620.0);
    }

    private void assertSavingAccountEqual(SavingAccount result, SavingAccount expected) {
        assertThat(result.getAccountNumber()).isEqualTo(expected.getAccountNumber());
        assertThat(result.getAccountHolderName()).isEqualTo(expected.getAccountHolderName());
        assertThat(result.getBalance()).isEqualTo(expected.getBalance());
        assertThat(result.getMinimumBalance()).isEqualTo(expected.getMinimumBalance());
        assertThat(result.getInterestRate()).isEqualTo(expected.getInterestRate());
        assertThat(result.getInterestPeriod()).isEqualTo(expected.getInterestPeriod());
    }
}