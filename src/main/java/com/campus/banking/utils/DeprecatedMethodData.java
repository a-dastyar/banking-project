package com.campus.banking.utils;

import java.lang.reflect.Method;

public record DeprecatedMethodData(Method method, String reason, String replacement) {

}
