package com.campus.banking.exception;

import lombok.experimental.StandardException;

@StandardException
public class LessThanMinimumTransactionException extends RuntimeException {

    static final String MESSAGE = "A transaction amount can not be less than minimum transaction amount";
    public static final LessThanMinimumTransactionException EXCEPTION;

    static {
        EXCEPTION = new LessThanMinimumTransactionException(MESSAGE);
    }
}
