package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.LessThanMinimumTransactionException;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.User;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
public class CheckingAccountServiceTest {

    @Mock
    BankAccountDAO<CheckingAccount> dao;

    @Mock
    UserService users;

    @Mock
    TransactionDAO trxDao;

    CheckingAccountService service;

    @BeforeEach
    void setup() {
        service = new CheckingAccountServiceImpl(dao, trxDao, users, 10);
    }

    @SuppressWarnings("unchecked")
    private Answer<Object> executeConsumer(InvocationOnMock invocation) {
        var consumer = (Consumer<EntityManager>) invocation.getArgument(0);
        consumer.accept(mock(EntityManager.class));
        return null;
    }

    @Test
    void add_withNullUser_shouldFail() {
        var account = CheckingAccount.builder()
                .accountNumber("3000")
                .build();
        assertThatThrownBy(() -> service.add(account)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_withNullUsername_shouldFail() {
        var account = CheckingAccount.builder()
                .accountHolder(User.builder().build())
                .accountNumber("3000")
                .build();
        assertThatThrownBy(() -> service.add(account)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_withBlankUsername_shouldFail() {
        var account = CheckingAccount.builder()
                .accountHolder(User.builder().username("").build())
                .accountNumber("3000")
                .build();
        assertThatThrownBy(() -> service.add(account)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_withBalanceLessThanTransactionFee_shouldFail() {
        var account = CheckingAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .balance(CheckingAccount.TRANSACTION_FEE - 1.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        assertThatThrownBy(() -> service.add(account)).isInstanceOf(LessThanMinimumTransactionException.class);
    }

    @Test
    void add_withBalance_shouldInsertTransaction() {
        var balance = CheckingAccount.TRANSACTION_FEE * 10;
        var account = CheckingAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .balance(balance)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        service.add(account);
        verify(trxDao, times(2)).transactionalPersist(any(), any());
        assertThat(account.getBalance()).isEqualTo(balance - CheckingAccount.TRANSACTION_FEE);
    }

    @Test
    void add_withValidAccount_shouldAdd() {
        var account = CheckingAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .build();
        service.add(account);
        assertThatNoException();
    }

    @Test
    void getByAccountNumber_withNullAccountNumber_shouldReturnAccount() {
        var account = CheckingAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .build();
        when(dao.findByAccountNumber(any())).thenReturn(Optional.of(account));
        var found = service.getByAccountNumber(account.getAccountNumber());
        assertThat(found.getAccountNumber()).isEqualTo(account.getAccountNumber());
    }

    @Test
    void withdraw_withAmountLessThanTransactionFee_shouldFail() {
        var accountNumber = "4000";
        assertThatThrownBy(() -> service.withdraw(accountNumber, CheckingAccount.TRANSACTION_FEE - 1.0))
                .isInstanceOf(LessThanMinimumTransactionException.class);
    }

    @Test
    void withdraw_withZeroBalanceAndZeroOverdraftLimit_shouldFail() {
        var account = CheckingAccount.builder()
                .accountNumber("4000")
                .balance(0.0)
                .overdraftLimit(0.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> service.withdraw(account.getAccountNumber(), 200.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withEnoughBalanceButNotEnoughForTrxFee_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(1000.0)
                .overdraftLimit(0.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> service.withdraw(account.getAccountNumber(), 1000.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withZeroBalanceAndEnoughOverdraftButNotEnoughForTrxFee_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .overdraftLimit(1000.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> service.withdraw(account.getAccountNumber(), 1000.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withSomeBalanceAndSomeOverdraftButNotEnough_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(500.0)
                .overdraftLimit(500.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> service.withdraw(account.getAccountNumber(), 2000.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withSomeBalanceAndSomeOverdraftButNotEnoughForTrxFee_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(500.0)
                .overdraftLimit(500.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> service.withdraw(account.getAccountNumber(), 1000.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withEnoughBalanceButNoTrxFeeLeftForDeposit_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(500.0)
                .overdraftLimit(0.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> service.withdraw(account.getAccountNumber(), 400.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withZeroBalanceButEnoughOverdraftLimit_shouldWithdraw() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .overdraftLimit(1000.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.withdraw(account.getAccountNumber(), 200.0);
        assertThat(account.getDebt()).isEqualTo(200.0 +
                CheckingAccount.TRANSACTION_FEE);
    }

    @Test
    void withdraw_withEnoughBalanceButNotEnoughForTrxFeeAndEnoughOverdraftLimit_shouldWithdraw() {
        var account = CheckingAccount.builder()
                .balance(1000.0)
                .overdraftLimit(1000.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.withdraw(account.getAccountNumber(), 1000.0);
        assertThat(account.getBalance()).isEqualTo(0.0d);
        assertThat(account.getDebt()).isEqualTo(CheckingAccount.TRANSACTION_FEE);
    }

    @Test
    void withdraw_withEnoughBalanceAndEnoughOverdraftLimit_shouldWithdraw() {
        var account = CheckingAccount.builder()
                .balance(1000.0)
                .overdraftLimit(1000.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.withdraw(account.getAccountNumber(), 500.0);
        assertThat(account.getBalance()).isEqualTo(500.0 -
                CheckingAccount.TRANSACTION_FEE);
        assertThat(account.getDebt()).isEqualTo(0.0d);
    }

    @Test
    void deposit_withLessThanDebt_shouldDeposit() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .debt(300.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.deposit(account.getAccountNumber(), 200.0);
        assertThat(account.getDebt()).isEqualTo(300.0 - (200.0 -
                CheckingAccount.TRANSACTION_FEE));
    }

    @Test
    void deposit_withAsMuchAsDebt_shouldDeposit() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .debt(300)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.deposit(account.getAccountNumber(), 300.0 + CheckingAccount.TRANSACTION_FEE);
        assertThat(account.getDebt()).isEqualTo(0.0);
    }

    @Test
    void deposit_withMoreThanDebt_shouldDeposit() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .debt(300)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.deposit(account.getAccountNumber(), 500.0 + CheckingAccount.TRANSACTION_FEE);
        assertThat(account.getDebt()).isEqualTo(0.0);
        assertThat(account.getBalance()).isEqualTo(200.0);
    }

    @Test
    void deposit_withNoDebt_shouldDeposit() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .debt(0.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.deposit(account.getAccountNumber(), 500.0 + CheckingAccount.TRANSACTION_FEE);
        assertThat(account.getDebt()).isEqualTo(0.0);
        assertThat(account.getBalance()).isEqualTo(500.0);
    }
}
