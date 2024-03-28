package com.campus.banking.exception;

import lombok.experimental.StandardException;

@StandardException
public class DuplicatedException extends RuntimeException {

    static final String DUPLICATED_USER_MESSAGE = "Username or email already exists";

    public static final DuplicatedException DUPLICATED_USER;

    static {
        DUPLICATED_USER = new DuplicatedException(DUPLICATED_USER_MESSAGE);
    }

}
