package com.campus.banking.service;

import jakarta.enterprise.context.Dependent;

@Dependent
class HashServiceImpl implements HashService{

    @Override
    public String hashOf(String str) {
        // TODO: Implement hashing
        return str;
    }

}
