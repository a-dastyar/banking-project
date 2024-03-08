package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.SQLIntegrityConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractIT;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;

public class CheckingAccountDAOIT extends AbstractIT {

    BankAccountDAO<BankAccount> bankDao;
    BankAccountDAO<CheckingAccount> dao;
    Database db;

    @BeforeEach
    void setup() {
        db = new DatabaseImpl(AbstractIT.ds);
        bankDao = new BankAccountDAOImpl(db);
        dao = new CheckingAccountDAOImpl(db, bankDao);
        db.clear();
    }

    @Test
    void add_withValidAccount_shouldAdd() {
        var account = CheckingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(0.0)
                .overDraftLimit(1000)
                .debt(0.0)
                .build();
        dao.add(account);
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();

        assertThat(account.getId()).isGreaterThan(0);

        assertEqual(found, account);
    }

    @Test
    void add_withDuplicatedAccountNumber_shouldAdd() {
        var account = CheckingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(0.0)
                .overDraftLimit(1000)
                .debt(0.0)
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
        var account = CheckingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(0.0)
                .overDraftLimit(1000)
                .debt(0.0)
                .build();
        dao.add(account);
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();

        assertEqual(found, account);
    }

    @Test
    void update_withAccount_shouldUpdate() {
        var account = CheckingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(100.0)
                .overDraftLimit(1000)
                .debt(0.0)
                .build();
        dao.add(account);
        account.setAccountHolderName("NEW");
        account.setAccountNumber("3000");
        account.setBalance(0.0);
        account.setDebt(100.0);
        account.setOverDraftLimit(500);
        dao.update(account);
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();

        assertEqual(found, account);
    }

    @Test
    void sumBalanceHigherThan_withOneAccountHigher_shouldReturnAccount() {
        var account = CheckingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(100.0)
                .overDraftLimit(1000)
                .debt(0.0)
                .build();
        var account2 = CheckingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("3000")
                .balance(2000.0)
                .overDraftLimit(1000)
                .debt(0.0)
                .build();
        dao.add(account);
        dao.add(account2);
        var sum = dao.sumBalanceHigherThan(1000.0);
        assertThat(sum).isEqualTo(account2.getBalance());
    }

    @Test
    void sumBalanceHigherThan_withTwoAccountHigher_shouldReturnAccount() {
        var account = CheckingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(200.0)
                .overDraftLimit(1000)
                .debt(0.0)
                .build();
        var account2 = CheckingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("3000")
                .balance(2000.0)
                .overDraftLimit(1000)
                .debt(0.0)
                .build();
        dao.add(account);
        dao.add(account2);
        var sum = dao.sumBalanceHigherThan(100.0);
        assertThat(sum).isEqualTo(account.getBalance() + account2.getBalance());
    }

    private void assertEqual(CheckingAccount found, CheckingAccount account) {
        assertThat(found.getAccountNumber()).isEqualTo(account.getAccountNumber());
        assertThat(found.getAccountHolderName()).isEqualTo(account.getAccountHolderName());
        assertThat(found.getBalance()).isEqualTo(account.getBalance());
        assertThat(found.getOverDraftLimit()).isEqualTo(account.getOverDraftLimit());
        assertThat(found.getDebt()).isEqualTo(account.getDebt());
    }
}