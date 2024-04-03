package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.campus.banking.exception.InvalidArgumentException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.User;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.Page;
import com.campus.banking.persistence.TransactionDAO;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractAccountServiceTest<T extends BankAccount> {

    protected BankAccountService<T> service;

    @Mock
    protected TransactionDAO trxDao;

    protected BankAccountDAO<T> dao;

    @SuppressWarnings("unchecked")
    protected Answer<Object> executeConsumer(InvocationOnMock invocation) {
        var consumer = (Consumer<EntityManager>) invocation.getArgument(0);
        consumer.accept(mock(EntityManager.class));
        return null;
    }

    @Test
    void getByAccountNumber_withNullAccountNumber_shouldReturnAccount() {
        var account = generate(1).findFirst().get();
        account.setAccountNumber(null);
        when(dao.findByAccountNumber(any())).thenReturn(Optional.of((T) account));
        var found = service.getByAccountNumber(account.getAccountNumber());
        assertThat(found.getAccountNumber()).isEqualTo(account.getAccountNumber());
    }

    @Test
    void add_withNullUser_shouldFail() {
        var account = generate(1).findFirst().get();
        account.setAccountHolder(null);
        assertThatThrownBy(() -> service.add((T) account)).isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    void add_withNullUsername_shouldFail() {
        var account = generate(1).findFirst().get();
        account.setAccountHolder(User.builder().build());
        assertThatThrownBy(() -> service.add(account)).isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    void add_withBlankUsername_shouldFail() {
        var account = generate(1).findFirst().get();
        account.setAccountHolder(User.builder().username("").build());
        assertThatThrownBy(() -> service.add(account)).isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    void getByUsername_withNoSize_shouldUseDefaultSize() {
        var page = new Page<T>(generate(10).toList(), 10, 1, 10);
        when(dao.findByUsername(any(), anyInt(), anyInt())).thenReturn(page);
        var found = service.getByUsername("test", 1, Optional.empty());
        assertThat(found.size()).isEqualTo(page.size());
    }

    @Test
    void getPage_withNoSize_shouldUseDefaultSize() {
        var page = new Page<T>(generate(10).toList(), 10, 1, 10);
        when(dao.getAll(anyInt(), anyInt())).thenReturn(page);
        var found = service.getPage(1, Optional.empty());
        assertThat(found.size()).isEqualTo(page.size());
    }

    @Test
    void sumBalance_shouldReturnSum() {
        when(dao.sumBalanceHigherThan(anyDouble())).thenReturn(1000.0);
        var sum = service.sumBalanceHigherThan(10);
        assertThat(sum).isEqualTo(1000.0);
    }

    @Test
    void getTransactions_withNonExistingAccount_shouldTransactions() {
        when(dao.findByAccountNumber(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getTransactions("", 1, Optional.empty()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getTransactions_withExistingAccount_shouldTransactions() {
        var page = new Page<Transaction>(List.of(), 0, 0, 0);
        when(dao.findByAccountNumber(any())).thenReturn(generate(1).findFirst());
        when(trxDao.findByOrdered(any(), any(), anyInt(), anyInt(), any(), any())).thenReturn(page);
        var transactions=service.getTransactions("", 1, Optional.empty());
        assertThat(transactions.list()).isEmpty();
    }

    abstract Stream<T> generate(int count);
}
