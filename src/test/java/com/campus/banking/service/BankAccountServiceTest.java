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

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceTest {

    @Mock
    BankAccountDAO<BankAccount> dao;

    @Mock
    TransactionDAO trxDao;

    BankAccountService<BankAccount> service;

    @BeforeEach
    void setup() {
        service = new BankAccountServiceImpl(dao, trxDao);
    }

    @SuppressWarnings("unchecked")
    private Answer<Object> executeConsumer(InvocationOnMock invocation) {
        var consumer = (Consumer<EntityManager>) invocation.getArgument(0);
        consumer.accept(mock(EntityManager.class));
        return null;
    }

    @Test
    void add_withValidAccount_shouldAdd() {
        var account = BankAccount.builder()
                .accountHolderName("Tester")
                .accountNumber("3000")
                .build();
        service.add(account);
        assertThatNoException();
    }

    @Test
    void getByAccountNumber_withNullAccountNumber_shouldReturnAccount() {
        var account = BankAccount.builder()
                .accountHolderName("Tester")
                .accountNumber("3000")
                .build();
        when(dao.findByAccountNumber(any())).thenReturn(Optional.of(account));
        var found = service.getByAccountNumber(account.getAccountNumber());
        assertThat(found.getAccountNumber()).isEqualTo(account.getAccountNumber());
    }

    @Test
    void withdraw_withMoreThanBalance_shouldFail() {
        var account = BankAccount.builder()
                .accountNumber("3000")
                .accountHolderName("Test")
                .balance(10.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> service.withdraw(account.getAccountNumber(), 11.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withLessThanBalance_shouldWithdraw() {
        var account = BankAccount.builder()
                .accountHolderName("Tester")
                .accountNumber("3000")
                .balance(10.0)
                .build();

        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.withdraw(account.getAccountNumber(), 9.0);
        assertThat(account.getBalance()).isEqualTo(1.0);
    }

    @Test
    void deposit_withPositiveAmount_shouldDeposit() {
        var account = BankAccount.builder()
                .accountHolderName("Tester")
                .accountNumber("3000")
                .balance(10.0)
                .build();

        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.deposit(account.getAccountNumber(), 10.0);
        assertThat(account.getBalance()).isEqualTo(20.0);
    }
}
