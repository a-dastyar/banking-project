package com.campus.banking.exception;

import lombok.experimental.StandardException;

@StandardException
public class InsufficientFundsException extends RuntimeException {

    static final String MESSAGE = "Insufficient funds for transaction";
    public static final InsufficientFundsException EXCEPTION;

    static {
        EXCEPTION = new InsufficientFundsException(MESSAGE);
    }

}
