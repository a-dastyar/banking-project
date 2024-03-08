package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.SQLIntegrityConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractIT;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;

public class SavingAccountDAOIT extends AbstractIT {

    BankAccountDAO<BankAccount> bankDao;
    SavingAccountDAO dao;
    Database db;

    @BeforeEach
    void setup() {
        db = new DatabaseImpl(AbstractIT.ds);
        bankDao = new BankAccountDAOImpl(db);
        dao = new SavingAccountDAOImpl(db, bankDao);
        db.clear();
    }

    @Test
    void add_withValidAccount_shouldAdd() {
        var account = SavingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(100.0)
                .minimumBalance(10.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .interestRate(10.0)
                .build();
        dao.add(account);
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();
        assertThat(account.getId()).isGreaterThan(0);

        assertEqual(found, account);
    }

    @Test
    void add_withDuplicatedAccountNumber_shouldAdd() {
        var account = SavingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(100.0)
                .minimumBalance(10.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .interestRate(10.0)
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
        var account = SavingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(100.0)
                .minimumBalance(10.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .interestRate(10.0)
                .build();
        dao.add(account);
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();

        assertEqual(found, account);
    }

    @Test
    void update_withAccount_shouldUpdate() {
        var account = SavingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(100.0)
                .minimumBalance(10.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .interestRate(10.0)
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
        var account = SavingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(100.0)
                .minimumBalance(10.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .interestRate(10.0)
                .build();
        var account2 = SavingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("3000")
                .balance(2000.0)
                .minimumBalance(10.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .interestRate(10.0)
                .build();
        dao.add(account);
        dao.add(account2);
        var sum = dao.sumBalanceHigherThan(1000.0);
        assertThat(sum).isEqualTo(account2.getBalance());
    }

    @Test
    void sumBalanceHigherThan_withTwoAccountHigher_shouldReturnAccount() {
        var account = SavingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(200.0)
                .minimumBalance(10.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .interestRate(10.0)
                .build();
        var account2 = SavingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("3000")
                .balance(2000.0)
                .minimumBalance(10.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .interestRate(10.0)
                .build();
        dao.add(account);
        dao.add(account2);
        var sum = dao.sumBalanceHigherThan(100.0);
        assertThat(sum).isEqualTo(account.getBalance() + account2.getBalance());
    }

    @Test
    void applyInterest() {
        var account = SavingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("2000")
                .balance(200.0)
                .minimumBalance(10.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .interestRate(10.0)
                .build();
        var account2 = SavingAccount.builder()
                .accountHolderName("Test")
                .accountNumber("3000")
                .balance(2000.0)
                .minimumBalance(10.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .interestRate(10.0)
                .build();
        dao.add(account);
        dao.add(account2);
        dao.applyInterest();
        var sum = dao.sumBalanceHigherThan(0);
        var expected = 220.0 + 2200.0;
        assertThat(sum).isEqualTo(expected);
    }

    private void assertEqual(SavingAccount found, SavingAccount account) {
        assertThat(found.getAccountNumber()).isEqualTo(account.getAccountNumber());
        assertThat(found.getAccountHolderName()).isEqualTo(account.getAccountHolderName());
        assertThat(found.getBalance()).isEqualTo(account.getBalance());
        assertThat(found.getMinimumBalance()).isEqualTo(account.getMinimumBalance());
        assertThat(found.getInterestRate()).isEqualTo(account.getInterestRate());
        assertThat(found.getInterestPeriod()).isEqualTo(account.getInterestPeriod());
    }
}