package com.campus.banking.exception;

import lombok.experimental.StandardException;

@StandardException
public class InvalidTransactionException extends RuntimeException {
    
    static final String WITHDRAW_MORE_THAN_ALLOWED_MESSAGE = "Can not withdraw more than allowed amount";
    public static final InvalidTransactionException WITHDRAW_MORE_THAN_ALLOWED;

    static {
        WITHDRAW_MORE_THAN_ALLOWED = new InvalidTransactionException(WITHDRAW_MORE_THAN_ALLOWED_MESSAGE);
    }
}
