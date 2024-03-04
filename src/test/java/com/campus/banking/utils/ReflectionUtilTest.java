package com.campus.banking.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ReflectionUtilTest {

    class classWithNoDeprecatedMethod {
        void nonDeprecatedMethod() {}
    }

    class classWithDeprecatedMethod {
        @DeprecatedMethod(
            reason = "for no reason",
            replacement = "nonDeprecatedMethod"
        )
        void deprecatedMethod() {}
        void nonDeprecatedMethod() {}
    }

    @Test
    void getDeprecatedMethods_onClassWithNoDeprecatedMethod_shouldGetEmptyList() {
        var deprecatedMethods = ReflectionUtil.getDeprecatedMethods(classWithNoDeprecatedMethod.class);
        assertThat(deprecatedMethods).isEmpty();
    }

    @Test
    void getDeprecatedMethods_onClassWithDeprecatedMethod_shouldGetDeprecatedMethodDatas() {
        var deprecatedMethods = ReflectionUtil.getDeprecatedMethods(classWithDeprecatedMethod.class);
        assertThat(deprecatedMethods.size()).isEqualTo(1);
        assertThat(deprecatedMethods.get(0).reason()).isEqualTo("for no reason");
        assertThat(deprecatedMethods.get(0).replacement()).isEqualTo("nonDeprecatedMethod");
    }

    @Test
    void getDeprecatedMethods_onClassWithNoDeprecatedMethodAndClassWithDeprecatedMethod_shouldGetDeprecatedMethodDatas() {
        var deprecatedMethods = ReflectionUtil.getDeprecatedMethods(classWithNoDeprecatedMethod.class, classWithDeprecatedMethod.class);
        assertThat(deprecatedMethods.size()).isEqualTo(1);
        assertThat(deprecatedMethods.get(0).reason()).isEqualTo("for no reason");
        assertThat(deprecatedMethods.get(0).replacement()).isEqualTo("nonDeprecatedMethod");
    }
}
