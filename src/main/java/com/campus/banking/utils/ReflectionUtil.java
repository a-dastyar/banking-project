package com.campus.banking.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtil {

    public static List<DeprecatedMethodData> getDeprecatedMethods(Class<?>... classes) {
        return Arrays.stream(classes)
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods()))
                .filter(method -> method.isAnnotationPresent(DeprecatedMethod.class))
                .map(ReflectionUtil::extractDeprecationData)
                .toList();
    }

    private static DeprecatedMethodData extractDeprecationData(Method method) {
        var annotation = method.getAnnotation(DeprecatedMethod.class);
        String reason = annotation.reason();
        String replacement = annotation.replacement();
        return new DeprecatedMethodData(method, reason, replacement);
    }
}
