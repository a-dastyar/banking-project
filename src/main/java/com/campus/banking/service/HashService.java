package com.campus.banking.service;

public interface HashService {
    public String hashOf(String str);
    public boolean matches(String str, String hash);

}
