package com.campus.banking.exception;

import lombok.experimental.StandardException;

@StandardException
public class NotFoundException extends RuntimeException {

    static final String USERNAME_NOT_FOUND_MESSAGE = "User with given username doesn't exists";
    public static final NotFoundException USERNAME_NOT_FOUND;
    
    static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account with given account number doesn't exists";
    public static final NotFoundException ACCOUNT_NOT_FOUND;

    static {
        USERNAME_NOT_FOUND = new NotFoundException(USERNAME_NOT_FOUND_MESSAGE);
        ACCOUNT_NOT_FOUND = new NotFoundException(ACCOUNT_NOT_FOUND_MESSAGE);
    }
}
