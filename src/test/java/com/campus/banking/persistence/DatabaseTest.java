package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.exception.LoadFailureException;
import com.campus.banking.exception.SaveFailureException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;

public class DatabaseTest {

    Database db = DatabaseImpl.INSTANCE;

    @AfterEach
    void teardown() {
        try {
            db.clear();
        } catch (SaveFailureException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void add_withNullAccount_shouldFail() {
        assertThatThrownBy(() -> db.add(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_withNullAccountNumber_shouldFail() {
        assertThatThrownBy(() -> db.add(new BankAccount())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_withValidAccount_shouldAdd() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("10000")
                .balance(10.0d).build();
        db.add(account);
        var fromDb = db.get(account.getAccountNumber());
        assertThat(fromDb).isEqualTo(account);
    }

    @Test
    void get_withNullId_shouldFail() {
        assertThatThrownBy(() -> db.get(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void get_withNoneExistingAccountNumber_shouldReturnNull() {
        var found = db.get("Doesn't exists");
        assertThat(found).isNull();
    }

    @Test
    void get_withValidAccountNumber_shouldReturnAccount() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("10000")
                .balance(10.0d).build();
        db.add(account);
        var found = db.get(account.getAccountNumber());
        assertThat(found).isEqualTo(account);
    }

    @Test
    void remove_withNullId_shouldFail() {
        assertThatThrownBy(() -> db.remove(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void remove_withValidAccountNumber_shouldRemove() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("10000")
                .balance(10.0d).build();
        db.add(account);
        db.remove(account.getAccountNumber());
        var found = db.get(account.getAccountNumber());
        assertThat(found).isNull();
    }

    @Test
    void list_withNoAccount_shouldReturnEmptyList() {
        var list = db.list();
        assertThat(list).isEmpty();
    }

    @Test
    void list_withAccounts_shouldReturnList() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("10000")
                .balance(10.0d).build();
        var account2 = BankAccount.builder()
                .accountHolderName("Test2")
                .accountNumber("20000")
                .balance(10.0d).build();
        db.add(account);
        db.add(account2);
        var list = db.list();
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void persist_withEmptyMap_shouldSaveToFile() {
        try {
            db.persist();
            db.load();
            assertThat(db.list()).isEmpty();
        } catch (LoadFailureException | SaveFailureException e) {
            fail();
        }
    }

    @Test
    void persist_withOneBankAccount_shouldSaveToFile() {
        try {
            var account = BankAccount.builder()
                    .accountHolderName("Test")
                    .accountNumber("10000")
                    .balance(10.0d).build();
            var account2 = BankAccount.builder()
                    .accountHolderName("Test2")
                    .accountNumber("20000")
                    .balance(10.0d).build();
            db.add(account);
            db.add(account2);
            db.persist();
            db.load();
            assertThat(db.list().size()).isEqualTo(2);
        } catch (LoadFailureException | SaveFailureException e) {
            fail();
        }
    }

    @Test
    void persist_withMultipleBankAccount_shouldSaveToFile() {
        try {
            var account = BankAccount.builder()
                    .accountHolderName("Test")
                    .accountNumber("10000")
                    .balance(10.0d).build();
            db.add(account);
            db.persist();
            db.load();
            var found = db.get(account.getAccountNumber());
            assertThat(found).isEqualTo(account);
        } catch (LoadFailureException | SaveFailureException e) {
            fail();
        }
    }

    @Test
    void persist_withSavingAccount_shouldSaveAccount() {
        try {
            var account = SavingAccount.builder()
                    .accountHolderName("Test")
                    .accountNumber("10000")
                    .balance(10.0d)
                    .interestPeriod(InterestPeriod.YEARLY)
                    .build();
            db.add(account);
            db.persist();
            db.load();
            var found = db.get(account.getAccountNumber());
            assertThat(found).isInstanceOf(SavingAccount.class);
            var savingAcc = (SavingAccount) found;
            assertThat(savingAcc.getInterestPeriod()).isEqualTo(InterestPeriod.YEARLY);
        } catch (LoadFailureException | SaveFailureException e) {
            fail();
        }
    }

    @Test
    void persist_withCheckingAccount_shouldSaveAccount() {
        try {
            var account = CheckingAccount.builder()
                    .accountHolderName("Test")
                    .accountNumber("10000")
                    .balance(10.0d)
                    .overDraftLimit(10.0d)
                    .build();
            db.add(account);
            db.persist();
            db.load();
            var found = db.get(account.getAccountNumber());
            assertThat(found).isInstanceOf(CheckingAccount.class);
            var savingAcc = (CheckingAccount) found;
            assertThat(savingAcc.getOverDraftLimit()).isEqualTo(savingAcc.getOverDraftLimit());
        } catch (LoadFailureException | SaveFailureException e) {
            fail();
        }
    }
}
