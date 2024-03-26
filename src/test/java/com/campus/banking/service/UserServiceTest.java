package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.campus.banking.exception.DuplicatedException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.model.Role;
import com.campus.banking.model.User;
import com.campus.banking.persistence.UserDAO;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDAO dao;

    private HashService hash = new Argon2HashService();

    private UserService service;

    @BeforeEach
    void setup() {
        service = new UserServiceImpl(dao, hash, 50, 10);
    }

    @SuppressWarnings("unchecked")
    private Answer<Object> executeConsumer(InvocationOnMock invocation) {
        var consumer = (Consumer<EntityManager>) invocation.getArgument(0);
        consumer.accept(mock(EntityManager.class));
        return null;
    }

    @Test
    void add_withDuplicatedIdentifiers_shouldFail() {
        var user = User.builder().build();
        when(dao.exists(user)).thenReturn(true);
        assertThatThrownBy(() -> service.add(user))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void add_withValidUser_shouldAdd() {
        var user = User.builder().password("secure").build();
        when(dao.exists(user)).thenReturn(false);
        service.add(user);
        assertThat(user.getPassword()).isNotEqualTo("secure");
        assertThatNoException();
    }

    @Test
    void signup_withDuplicatedIdentifiers_shouldFail() {
        var user = User.builder().build();
        when(dao.exists(user)).thenReturn(true);
        assertThatThrownBy(() -> service.add(user))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void signup_withValidUser_shouldAdd() {
        var user = User.builder().password("secure").build();
        when(dao.exists(user)).thenReturn(false);
        service.signup(user);
        assertThat(user.getPassword()).isNotEqualTo("secure");
        assertThat(user.getRoles().size()).isEqualTo(1);
        assertThat(user.getRoles()).contains(Role.MEMBER);
        assertThatNoException();
    }

    @Test
    void getByUsername_withNoUser_shouldFail() {
        assertThatThrownBy(() -> service.getByUsername("tester"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByUsername_withUser_shouldReturn() {
        var user = User.builder().username("tester").build();
        when(dao.findBy(any(), any())).thenReturn(List.of(user));
        var found = service.getByUsername("tester");
        assertThat(found.getUsername()).isEqualTo("tester");
    }

    @Test
    void update_withUser_shouldUpdate() {
        var user = User.builder().id(12L).username("tester").build();
        when(dao.findBy(any(), any())).thenReturn(List.of(user));
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        service.update(user);
        verify(dao, times(1)).transactionalUpdate(any(), any());
    }

    @Test
    void setupAdminAccount_withNoUser_shouldAdd() {
        when(dao.countAll()).thenReturn(0L);
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        service.setupAdminAccount();
        verify(dao, times(1)).transactionalPersist(any(), any());
    }

    @Test
    void setupAdminAccount_withUser_shouldAdd() {
        when(dao.countAll()).thenReturn(10L);
        service.setupAdminAccount();
        verify(dao, never()).transactionalPersist(any(), any());
    }

    @Test
    void toUser_withEmptyMap_shouldReturnEmptyUser() {
        var user = UserService.toUser(Map.of());
        assertThat(user.getEmail()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getRoles()).isEmpty();
    }

    @Test
    void toUser_withInvalidRole_shouldReturnEmptyRole() {
        var user = UserService.toUser(Map.of(
                "roles", new String[] { "INVALID" }));
        assertThat(user.getEmail()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getRoles()).isEmpty();
    }

    @Test
    void toUser_withFullUser_shouldReturnUser() {
        var user = UserService.toUser(Map.of(
                "username", new String[] { "admin" },
                "email", new String[] { "admin@bank.co" },
                "password", new String[] { "password" },
                "roles", new String[] { "ADMIN", "MANAGER" }));
        assertThat(user.getEmail()).isEqualTo("admin@bank.co");
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getRoles()).contains(Role.ADMIN, Role.MANAGER);
    }

}
