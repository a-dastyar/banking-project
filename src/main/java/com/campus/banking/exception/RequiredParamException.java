package com.campus.banking.exception;

import lombok.experimental.StandardException;

@StandardException
public class RequiredParamException extends RuntimeException {

    static final String MESSAGE = "The query parameter \"%s\" is required.";

    public static RequiredParamException getException(String param) {
        return new RequiredParamException(String.format(MESSAGE, param));
    }
}
