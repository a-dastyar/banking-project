package com.campus.banking.service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.campus.banking.model.Role;
import com.campus.banking.model.User;
import com.campus.banking.persistence.Page;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface UserService {

    public static User toUser(Map<String, String[]> properties) {
        var rolesName = Stream.of(Role.values()).map(Role::toString);
        var roles = Optional.ofNullable(properties.get("roles"))
                .map(Stream::of).orElse(Stream.empty())
                .filter(role -> rolesName.anyMatch(role::equals))
                .map(Role::valueOf)
                .collect(Collectors.toSet());
        return User.builder()
                .email(Stream.of(properties.get("email")).findFirst().orElse(null))
                .username(Stream.of(properties.get("username")).findFirst().orElse(null))
                .password(Stream.of(properties.get("password")).findFirst().orElse(null))
                .roles(roles)
                .build();
    }

    User getById(@Positive long id);

    void removeById(@Positive long id);

    void addUser(@NotNull @Valid User user);

    void updateUser(@NotNull @Valid User user);

    Page<User> getAll(@Min(1) int page);
}
