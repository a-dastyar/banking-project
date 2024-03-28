package com.campus.banking.exception;

import lombok.experimental.StandardException;

@StandardException
public class InvalidArgumentException extends RuntimeException {

    static final String BLANK_USERNAME_MESSAGE = "Username can not be blank";
    public static final InvalidArgumentException BLANK_USERNAME;

    static final String INVALID_TRANSACTION_TYPE_MESSAGE = "Invalid transaction type";
    public static final InvalidArgumentException INVALID_TRANSACTION_TYPE;

    static final String NON_NUMERIC_VALUE_MESSAGE = "Non-numeric value";
    public static final InvalidArgumentException NON_NUMERIC_VALUE;

    static final String NON_POSITIVE_INTEGER_MESSAGE = "Non-positive integer value";
    public static final InvalidArgumentException NON_POSITIVE_INTEGER;

    static {
        BLANK_USERNAME = new InvalidArgumentException(BLANK_USERNAME_MESSAGE);
        INVALID_TRANSACTION_TYPE = new InvalidArgumentException(INVALID_TRANSACTION_TYPE_MESSAGE);
        NON_NUMERIC_VALUE = new InvalidArgumentException(NON_NUMERIC_VALUE_MESSAGE);
        NON_POSITIVE_INTEGER = new InvalidArgumentException(NON_POSITIVE_INTEGER_MESSAGE);
    }
}
