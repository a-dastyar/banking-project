package com.campus.banking.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;


public class UtilsTest {

    @Test
    void first_withNullVal_shouldReturnEmpty() {
        var first = Utils.first(Map.of(), "test");
        assertThat(first.isEmpty()).isTrue();
    }

    @Test
    void first_withEmptyArray_shouldReturnEmpty() {
        var map = Map.of("test", new String[] { });
        var first = Utils.first(map, "test");
        assertThat(first.isEmpty()).isTrue();
    }

    @Test
    void first_withOneInArray_shouldReturnFirst() {
        var map = Map.of("test", new String[] { "first"});
        var first = Utils.first(map, "test");
        assertThat(first.get()).isEqualTo("first");
    }

    @Test
    void first_withTwoInArray_shouldReturnFirst() {
        var map = Map.of("test", new String[] { "first", "second" });
        var first = Utils.first(map, "test");
        assertThat(first.get()).isEqualTo("first");
    }

    @Test
    void firstDouble_withNullVal_shouldReturnEmpty() {
        var first = Utils.firstDouble(Map.of(), "test");
        assertThat(first.isEmpty()).isTrue();
    }

    @Test
    void firstDouble_withEmptyArray_shouldReturnEmpty() {
        var map = Map.of("test", new String[] { });
        var first = Utils.firstDouble(map, "test");
        assertThat(first.isEmpty()).isTrue();
    }

    @Test
    void firstDouble_withNonNumeric_shouldReturnFirst() {
        var map = Map.of("test", new String[] { "first"});
        var first = Utils.firstDouble(map, "test");
        assertThat(first.isEmpty()).isTrue();
    }

    @Test
    void firstDouble_withOneInArray_shouldReturnFirst() {
        var map = Map.of("test", new String[] { "1.0"});
        var first = Utils.firstDouble(map, "test");
        assertThat(first.getAsDouble()).isEqualTo(1.0);
    }

    @Test
    void firstDouble_withTwoInArray_shouldReturnFirst() {
        var map = Map.of("test", new String[] { "1.0", "2.0" });
        var first = Utils.firstDouble(map, "test");
        assertThat(first.getAsDouble()).isEqualTo(1.0);
    }
}
