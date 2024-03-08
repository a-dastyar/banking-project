package com.campus.banking.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.campus.banking.AbstractIT;
import com.campus.banking.model.BankAccount;

import java.sql.SQLIntegrityConstraintViolationException;

public class BankAccountDAOIT extends AbstractIT {

    BankAccountDAO<BankAccount> dao;
    Database db;

    @BeforeEach
    void setup() {
        db = new DatabaseImpl(AbstractIT.ds);
        dao = new BankAccountDAOImpl(db);
        db.clear();
    }

    @Test
    void add_withValidAccount_shouldAdd() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(10.0)
                .build();
        dao.add(account);
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();
        assertThat(account.getId()).isGreaterThan(0);

        assertEqual(found, account);
    }

    @Test
    void add_withDuplicatedAccountNumber_shouldAdd() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(10.0)
                .build();
        dao.add(account);
        assertThatThrownBy(() -> dao.add(account)).cause()
                .isInstanceOf(SQLIntegrityConstraintViolationException.class)
                .hasMessageContaining("Duplicate");
    }

    @Test
    void findByAccountNumber_withNoAccount_shouldReturnNull() {
        var account = dao.findByAccountNumber("2000");
        assertThat(account.isPresent()).isFalse();
    }

    @Test
    void findByAccountNumber_withAccount_shouldReturnAccount() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(10.0)
                .build();
        dao.add(account);
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();

        assertEqual(found, account);
    }

    @Test
    void update_withAccount_shouldUpdate() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(10.0)
                .build();
        dao.add(account);
        account.setAccountHolderName("NEW");
        account.setAccountNumber("3000");
        account.setBalance(100.0);
        dao.update(account);
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();

        assertEqual(found, account);
    }

    @Test
    void sumBalanceHigherThan_withOneAccountHigher_shouldReturnAccount() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(10.0)
                .build();
        var account2 = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("3000")
                .balance(5000.0)
                .build();
        dao.add(account);
        dao.add(account2);
        var sum = dao.sumBalanceHigherThan(1000.0);
        assertThat(sum).isEqualTo(account2.getBalance());
    }

    @Test
    void sumBalanceHigherThan_withTwoAccountHigher_shouldReturnAccount() {
        var account = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(200.0)
                .build();
        var account2 = BankAccount.builder()
                .accountHolderName("Test")
                .accountNumber("3000")
                .balance(5000.0)
                .build();
        dao.add(account);
        dao.add(account2);
        var sum = dao.sumBalanceHigherThan(100.0);
        assertThat(sum).isEqualTo(account.getBalance() + account2.getBalance());
    }

    private void assertEqual(BankAccount found, BankAccount account) {
        assertThat(found.getAccountNumber()).isEqualTo(account.getAccountNumber());
        assertThat(found.getAccountHolderName()).isEqualTo(account.getAccountHolderName());
        assertThat(found.getBalance()).isEqualTo(account.getBalance());
    }
}