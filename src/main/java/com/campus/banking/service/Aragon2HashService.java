package com.campus.banking.service;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import jakarta.enterprise.context.Dependent;

@Dependent
public class Aragon2HashService implements HashService {

    Argon2PasswordEncoder argon2 = new Argon2PasswordEncoder(16, 32, 1, 60000, 10);

    @Override
    public String hashOf(String str) {
        return argon2.encode(str);
    }

    @Override
    public boolean matches(String str, String hash) {
        return argon2.matches(str, hash);
    }

    
}
