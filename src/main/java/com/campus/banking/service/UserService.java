package com.campus.banking.service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.campus.banking.model.Role;
import com.campus.banking.model.User;
import com.campus.banking.persistence.Page;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface UserService {

    public static User toUser(Map<String, String[]> properties) {
        var rolesName = Stream.of(Role.values()).map(Role::toString).toList();
        var roles = Optional.ofNullable(properties.get("roles"))
                .map(Stream::of).orElse(Stream.empty())
                .filter(role -> rolesName.stream().anyMatch(role::equals))
                .map(Role::valueOf)
                .collect(Collectors.toSet());
        var email = Stream.of(Optional.ofNullable(properties.get("email"))
                .orElse(new String[] {})).findFirst().orElse(null);
        String username = Stream.of(Optional.ofNullable(properties.get("username"))
                .orElse(new String[] {})).findFirst().orElse(null);
        String password = Stream.of(Optional.ofNullable(properties.get("password"))
                .orElse(new String[] {})).findFirst().orElse(null);
        return User.builder()
                .email(email)
                .username(username)
                .password(password)
                .roles(roles)
                .build();
    }

    User getById(@Positive long id);

    User getByUsername(@NotNull @NotBlank String username);

    void removeById(@Positive long id);

    void add(@NotNull @Valid User user);

    void signup(@NotNull @Valid User user);

    void updateUser(@NotNull @Valid User user);

    Page<User> getAll(@Positive int page);
}
