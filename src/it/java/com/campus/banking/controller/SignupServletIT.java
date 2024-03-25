package com.campus.banking.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractHttpIT;
import com.campus.banking.util.HttpUtils.Response.Status;

public class SignupServletIT extends AbstractHttpIT {

    private String resource = "/signup";

    @Test
    void get_withoutLogin_shouldReturnSignupForm() {
        var client = http.clientBuilder().build();

        var request = http.GETRequestBuilder(http.resourceURI(resource))
                .GET().build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Status.Success);
        assertThat(response.httpResponse().body()).containsIgnoringCase("email");
    }

    @Test
    void get_withLogin_shouldReturnRedirectToHome() {
        var client = http.clientBuilder().build();

        var loginRes = http.login(client, "admin", "admin");
        assertThat(loginRes.status()).isEqualTo(Status.Success);
        assertThat(loginRes.httpResponse().statusCode()).isEqualTo(303);

        var request = http.GETRequestBuilder(http.resourceURI(resource))
                .timeout(Duration.ofMinutes(2))
                .GET().build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(302);
    }

    @Test
    void post_withLogin_shouldRedirectToHome() {
        var client = http.clientBuilder().build();

        var loginRes = http.login(client, "admin", "admin");
        assertThat(loginRes.status()).isEqualTo(Status.Success);
        assertThat(loginRes.httpResponse().statusCode()).isEqualTo(303);

        var form = Map.of(
                "username", "test",
                "email", "test@test.test",
                "password", "test");
        var signupRes = http.signup(client, form);

        assertThat(signupRes.status()).isEqualTo(Status.Success);
        assertThat(signupRes.httpResponse().statusCode()).isEqualTo(302);
        assertThat(signupRes.httpResponse().headers().firstValue("location").get())
                .isEqualTo(http.resourceURI("").getPath());
    }

    @Test
    void post_withoutLogin_shouldSignup() {
        var client = http.clientBuilder().build();

        var form = Map.of(
                "username", "test",
                "email", "test@test.test",
                "password", "test");
        var signupRes = http.signup(client, form);
        assertThat(signupRes.status()).isEqualTo(Status.Success);
        assertThat(signupRes.httpResponse().statusCode()).isEqualTo(302);
        assertThat(signupRes.httpResponse().headers().firstValue("location").get())
                .isEqualTo(http.resourceURI("/login").getPath());

        var loginRes = http.login(client, "test", "test");
        assertThat(loginRes.status()).isEqualTo(Status.Success);
        assertThat(loginRes.httpResponse().statusCode()).isEqualTo(303);
    }

    @Test
    void post_withInvalidUsername_shouldReturn400() {
        var client = http.clientBuilder().build();

        var form = Map.of(
                "username", "t",
                "email", "test@test.test",
                "password", "test");

        var signupRes = http.signup(client, form);
        assertThat(signupRes.status()).isEqualTo(Status.Success);
        assertThat(signupRes.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withInvalidPassword_shouldReturn400() {
        var client = http.clientBuilder().build();

        var form = Map.of(
                "username", "test",
                "email", "test@test.test",
                "password", "123");

        var signupRes = http.signup(client, form);
        assertThat(signupRes.status()).isEqualTo(Status.Success);
        assertThat(signupRes.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withInvalidEmail_shouldReturn400() {
        var client = http.clientBuilder().build();

        var form = Map.of(
                "username", "test",
                "email", "test@test",
                "password", "123");
        var signupRes = http.signup(client, form);

        assertThat(signupRes.status()).isEqualTo(Status.Success);
        assertThat(signupRes.httpResponse().statusCode()).isEqualTo(400);
    }
}
