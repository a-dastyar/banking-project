package com.campus.banking.util;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Stream;

public interface Utils {

    public static Optional<String> first(Map<String, String[]> map, String name) {
        return Stream.of(Optional.ofNullable(map.get(name))
                .orElse(new String[] {}))
                .findFirst();
    }

    public static OptionalDouble firstDouble(Map<String, String[]> map, String name) {
        return Stream.of(Optional.ofNullable(map.get(name))
                .orElse(new String[] {}))
                .filter(Utils::isDouble)
                .mapToDouble(Double::valueOf)
                .findFirst(); 
    }

    private static boolean isDouble(String str) {
        try {
            Double.valueOf(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}