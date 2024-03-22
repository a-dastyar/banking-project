package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class HashServiceTest {

    private HashService service = new Argon2HashService();

    @Test
    void hashOf_withRawString_shouldHash() {
        var input = "Test";
        var hash = service.hashOf(input);
        assertThat(input).isNotEqualTo(hash);
    }

    @Test
    void matches_withMatchingHash_shouldReturnTrue() {
        var input = "Test";
        var hash = service.hashOf(input);
        var matches = service.matches(input, hash);
        assertThat(matches).isTrue();
    }

    @Test
    void matches_withNonMatchingHash_shouldReturnFalse() {
        var input = "Test";
        var hash = service.hashOf(input);
        var matches = service.matches("Test2", hash);
        assertThat(matches).isFalse();
    }
}
