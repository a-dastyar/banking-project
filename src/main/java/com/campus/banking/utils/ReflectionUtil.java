package com.campus.banking.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {

    public static List<DeprecatedMethodData> getDeprecatedMethods(Class<?>... classes) {
        List<DeprecatedMethodData> datas = new ArrayList<>();

        for (var clazz : classes) {
            // TODO: check for clazz.getMethods() either?
            Method[] methods = clazz.getDeclaredMethods();

            for (var method : methods) {
                DeprecatedMethod annotation = method.getAnnotation(DeprecatedMethod.class);

                if (annotation != null) {
                    String reason = annotation.reason();
                    String replacement = annotation.replacement();
                    DeprecatedMethodData data = new DeprecatedMethodData(method, reason, replacement);

                    datas.add(data);
                }
            }
        }
        return datas;
    }
}
