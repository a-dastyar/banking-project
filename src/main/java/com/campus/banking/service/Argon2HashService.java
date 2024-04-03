package com.campus.banking.service;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Argon2HashService implements HashService {

    private final Argon2PasswordEncoder argon2 = new Argon2PasswordEncoder(16, 32, 1, 32768, 1);

    @Override
    public String hashOf(String str) {
        return argon2.encode(str);
    }

    @Override
    public boolean matches(String str, String hash) {
        return argon2.matches(str, hash);
    }
    
}
