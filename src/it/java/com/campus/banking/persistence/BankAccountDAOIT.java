package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractIT;
import com.campus.banking.model.BankAccount;

public class BankAccountDAOIT extends AbstractIT {

    BankAccountDAO<BankAccount> dao;

    @BeforeEach
    void setup() {
        super.beforeEach();
        dao = new BankAccountDAOImpl(super.db);
    }

    @AfterEach
    void teardown() {
        super.afterEach();
    }

    @Test
    void persist_withNullAccountNumber_shouldFail() {
        var account = BankAccount.builder()
                .accountHolderName("Tester")
                .balance(10.0).build();
        assertThatThrownBy(() -> dao.persist(account))
                .hasMessageContaining("null");
    }

    @Test
    void persist_withNullAccountHolderName_shouldFail() {
        var account = BankAccount.builder()
                .accountNumber("3000")
                .balance(10.0).build();
        assertThatThrownBy(() -> dao.persist(account))
                .hasMessageContaining("null");
    }

    @Test
    void persist_withValidAccount_shouldSave() {
        var account = BankAccount.builder()
                .accountNumber("3000")
                .accountHolderName("Tester")
                .balance(10.0).build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertBankAccountEqual(found, account);
    }

    @Test
    void find_withNoAccount_shouldReturnEmpty(){
        var found = dao.find(1L);
        assertThat(found).isEmpty();
    }

    @Test
    void find_withAccountId_shouldReturnAccount(){
        var account = BankAccount.builder()
                .accountNumber("3000")
                .accountHolderName("Tester")
                .balance(10.0).build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.find(account.getId()).get();
        assertBankAccountEqual(found, account);
    }

    
    @Test
    void findByAccountNumber_withNoAccount_shouldReturnEmpty(){
        var found = dao.findByAccountNumber("3000");
        assertThat(found).isEmpty();
    }

    @Test
    void findByAccountNumber_withAccountNumber_shouldReturnAccount(){
        var account = BankAccount.builder()
                .accountNumber("3000")
                .accountHolderName("Tester")
                .balance(10.0).build();
        dao.persist(account);
        assertThat(account.getId()).isNotNull();
        var found = dao.findByAccountNumber(account.getAccountNumber()).get();
        assertBankAccountEqual(found, account);
    }

    
    private void assertBankAccountEqual(BankAccount result, BankAccount expected) {
        assertThat(result.getAccountNumber()).isEqualTo(expected.getAccountNumber());
        assertThat(result.getAccountHolderName()).isEqualTo(expected.getAccountHolderName());
        assertThat(result.getBalance()).isEqualTo(expected.getBalance());
    }
}
