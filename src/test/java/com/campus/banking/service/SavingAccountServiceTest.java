package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
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

import com.campus.banking.exception.InvalidAccountException;
import com.campus.banking.exception.InvalidTransactionException;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.persistence.SavingAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
public class SavingAccountServiceTest {

    @Mock
    SavingAccountDAO dao;

    @Mock
    TransactionDAO trxDao;

    SavingAccountService service;

    @BeforeEach
    void setup() {
        service = new SavingAccountServiceImpl(dao, trxDao);
    }

    @SuppressWarnings("unchecked")
    private Answer<Object> executeConsumer(InvocationOnMock invocation) {
        var consumer = (Consumer<EntityManager>) invocation.getArgument(0);
        consumer.accept(mock(EntityManager.class));
        return null;
    }

    @Test
    void add_withNull_shouldFail() {
        assertThatThrownBy(() -> service.add(null)).isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void add_withAccountWithoutAccountNumber_shouldFail() {
        var account = SavingAccount.builder()
                .accountHolderName("Tester")
                .build();
        assertThatThrownBy(() -> service.add(account)).isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void add_withAccountWithBlankAccountNumber_shouldFail() {
        var account = SavingAccount.builder()
                .accountHolderName("Tester")
                .accountNumber("")
                .build();
        assertThatThrownBy(() -> service.add(account))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void add_withAccountWithoutAccountHolderName_shouldFail() {
        var account = SavingAccount.builder()
                .accountNumber("3000")
                .build();
        assertThatThrownBy(() -> service.add(account))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void add_withAccountWithBlankAccountHolderName_shouldFail() {
        var account = SavingAccount.builder()
                .accountNumber("3000")
                .accountHolderName("")
                .build();
        assertThatThrownBy(() -> service.add(account))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void add_withNegativeBalance_shouldFail() {
        var account = SavingAccount.builder()
                .accountNumber("3000")
                .accountHolderName("Test")
                .balance(-1.0)
                .build();
        assertThatThrownBy(() -> service.add(account))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void add_withValidAccount_shouldAdd() {
        var account = SavingAccount.builder()
                .accountHolderName("Tester")
                .accountNumber("3000")
                .build();
        service.add(account);
        assertThatNoException();
    }

    @Test
    void getByAccountNumber_withNullAccountNumber_shouldFail() {
        assertThatThrownBy(() -> service.getByAccountNumber(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getByAccountNumber_withNullAccountNumber_shouldReturnAccount() {
        var account = SavingAccount.builder()
                .accountHolderName("Tester")
                .accountNumber("3000")
                .build();
        when(dao.findByAccountNumber(any())).thenReturn(Optional.of(account));
        var found = service.getByAccountNumber(account.getAccountNumber());
        assertThat(found.getAccountNumber()).isEqualTo(account.getAccountNumber());
    }

    @Test
    void withdraw_withNegativeAmount_shouldFail() {
        var accountNumber = "5000";
        assertThatThrownBy(() -> service.withdraw(accountNumber, -10.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void withdraw_withMoreThanBalance_shouldFail() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .build();
        var accountNumber = "5000";
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> service.withdraw(accountNumber, 11.0))
                .isInstanceOf(InvalidTransactionException.class);
    }

    @Test
    void withdraw_withMoreThanMinimumBalance_shouldFail() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(5.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> service.withdraw(account.getAccountNumber(), 6.0))
                .isInstanceOf(InvalidTransactionException.class);
    }

    @Test
    void withdraw_withLessThanBalanceAndMinimumBalance_shouldWithdraw() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(5.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.withdraw(account.getAccountNumber(), 1.0);
        assertThat(account.getBalance()).isEqualTo(9.0);
    }

    @Test
    void deposit_withNegativeAmount_shouldFail() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(0.0)
                .build();
        assertThatThrownBy(() -> service.deposit(account.getAccountNumber(), -10.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deposit_withPositiveAmount_shouldDeposit() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(0.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.deposit(account.getAccountNumber(), 10.0);
        assertThat(account.getBalance()).isEqualTo(20.0);
    }

    @Test
    void applyInterest_withZeroBalance_shouldNotChangeBalance() {
        var balance = 0.0d;
        var account = SavingAccount.builder()
                .balance(balance)
                .minimumBalance(0.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.applyInterest(account.getAccountNumber());
        assertThat(account.getBalance()).isEqualTo(balance);
    }

    @Test
    void applyInterest_withPositiveBalance_shouldAddToBalance() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .interestRate(10)
                .minimumBalance(0.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.applyInterest(account.getAccountNumber());
        assertThat(account.getBalance()).isEqualTo(11.0);
    }

}