package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.campus.banking.exception.InvalidArgumentException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.User;
import com.campus.banking.persistence.BankAccountDAO;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractAccountServiceTest<T extends BankAccount> {

    protected BankAccountService<T> service;

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

    abstract Stream<T> generate(int count);
}
