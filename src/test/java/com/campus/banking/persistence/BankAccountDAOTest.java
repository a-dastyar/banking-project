package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.exception.InvalidAccountException;
import com.campus.banking.exception.SaveFailureException;
import com.campus.banking.model.BankAccount;

public class BankAccountDAOTest {

    Database db = DatabaseImpl.INSTANCE;
    BankAccountDAO<BankAccount> dao=new BankAccountDAOImpl(db);

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
        assertThatThrownBy(() -> dao.add(null)).isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void add_withNullAccountNumber_shouldFail() {
        assertThatThrownBy(() -> dao.add(new BankAccount())).isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void add_withValidAccount_shouldAdd() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("10000")
                .balance(10.0d).build();
        dao.add(account);
        var fromDb = dao.findByAccountNumber(account.getAccountNumber());
        assertThat(fromDb.get()).isEqualTo(account);
    }

    @Test
    void findByAccountNumber_withNullAccountNumber_shouldFail() {
        assertThatThrownBy(() -> dao.findByAccountNumber(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findByAccountNumber_withNoneExistingAccountNumber_shouldReturnNull() {
        var found = dao.findByAccountNumber("Doesn't exists");
        assertThat(found.isPresent()).isFalse();
    }

    @Test
    void findByAccountNumber_withValidAccountNumber_shouldReturnAccount() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("10000")
                .balance(10.0d).build();
        dao.add(account);
        var found = dao.findByAccountNumber(account.getAccountNumber());
        assertThat(found.get()).isEqualTo(account);
    }

    @Test
    void removeByAccountNumber_withValidAccountNumber_shouldRemove() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("10000")
                .balance(10.0d).build();
        dao.add(account);
        dao.removeByAccountNumber(account.getAccountNumber());
        var found = db.get(account.getAccountNumber(),BankAccount.class);
        assertThat(found).isNull();
    }

    @Test
    void list_withNoAccount_shouldReturnEmptyList() {
        var list = dao.list();
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
        dao.add(account);
        dao.add(account2);
        var list = dao.list();
        assertThat(list.size()).isEqualTo(2);
    }
}
