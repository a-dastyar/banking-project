package com.campus.banking.service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.campus.banking.model.Role;
import com.campus.banking.model.User;
import com.campus.banking.persistence.Page;
import com.campus.banking.util.Utils;

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

        var email = Utils.first(properties, "email")
                .orElse(null);

        var username = Utils.first(properties, "username")
                .orElse(null);

        var password = Utils.first(properties, "password")
                .orElse(null);
                
        return User.builder()
                .email(email)
                .username(username)
                .password(password)
                .roles(roles)
                .build();
    }

    User getByUsername(@NotNull @NotBlank String username);

    void add(@NotNull @Valid User user);

    void signup(@NotNull @Valid User user);

    void update(@NotNull @Valid User user);

    Page<User> getAll(@Positive int page);

    void setupAdminAccount();
}
