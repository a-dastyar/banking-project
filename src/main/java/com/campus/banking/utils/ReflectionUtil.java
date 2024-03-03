package com.campus.banking.utils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

public class ReflectionUtil {

    public static List<DeprecatedMethodData> getDeprecatedMethods(Class<?>... classes) {
        return Stream.of(classes)
        .<Method> mapMulti((clazz, consumer) -> {
            Stream.of(clazz.getDeclaredMethods()).forEach(consumer);
        })
        .filter((method) -> method.isAnnotationPresent(DeprecatedMethod.class))
        .map((method) -> {
            var annotation = method.getAnnotation(DeprecatedMethod.class);
            return new DeprecatedMethodData(method, annotation.reason(), annotation.replacement());
        })
        .toList();
    }
}
