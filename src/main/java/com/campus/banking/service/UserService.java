package com.campus.banking.service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.campus.banking.model.Role;
import com.campus.banking.model.User;
import com.campus.banking.persistence.Page;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface UserService {

    public static User toUser(Map<String, String[]> properties) {
        var rolesName =  Arrays.stream(Role.values()).map(Role::toString);
        var roles = Arrays.stream(properties.get("roles"))
                .filter(role -> rolesName.anyMatch(role::equals))
                .map(Role::valueOf)
                .collect(Collectors.toSet());
        return User.builder()
                .email(Arrays.stream(properties.get("email")).findFirst().orElse(null))
                .username(Arrays.stream(properties.get("username")).findFirst().orElse(null))
                .password(Arrays.stream(properties.get("password")).findFirst().orElse(null))
                .roles(roles)
                .build();
    }

    User getById(@Positive long id);

    void removeById(@Positive long id);

    void addUser(@NotNull @Valid User user);

    void updateUser(@NotNull @Valid User user);

    Page<User> getAll(@Min(1) int page);
}
