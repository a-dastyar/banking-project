package com.campus.banking.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractHttpIT;
import com.campus.banking.util.HttpUtils.Response;
import com.campus.banking.util.HttpUtils.Response.Status;

public class UserServletIT extends AbstractHttpIT{

    private String resource = "/users";

    @Test
    void get_withoutLogin_shouldReturn401() {
        var client = http.clientBuilder().build();
        var request = http.GETRequestBuilder(http.resourceURI(resource)).build();
        var response = http.sendRequest(client, request);
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(401);
    }

    @Test
    void get_withLoginAsMember_shouldReturn401() {
        var client = http.clientBuilder().build();

        var form = Map.of(
                "username", "test",
                "email", "test@test.test",
                "password", "test");
        var signupRes = http.signup(client, form);
        assertThat(signupRes.status()).isEqualTo(Status.Success);
        assertThat(signupRes.httpResponse().statusCode()).isEqualTo(302);

        var loginResponse = http.login(client, "test", "test");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var request = http.GETRequestBuilder(http.resourceURI(resource)).build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(403);
    }

    @Test
    void get_withLoginAsAdmin_shouldReturnPage() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var request = http.GETRequestBuilder()
                .uri(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(2))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("Users","admin");
    }

    @Test
    void post_withInvalidUsername_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);


        var form = Map.of(
                "username", "t",
                "email", "test@test.test",
                "password", "test");

        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(form))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withDuplicatedUsername_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);


        var form = Map.of(
                "username", "admin",
                "email", "test@test.test",
                "password", "test");

        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(form))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withInvalidEmail_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);


        var form = Map.of(
                "username", "test",
                "email", "test@test",
                "password", "test");

        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(form))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withDuplicatedEmail_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);


        var form = Map.of(
                "username", "test",
                "email", "admin@bank.co",
                "password", "test");

        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(form))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withInvalidPassword_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);


        var form = Map.of(
                "username", "test",
                "email", "test@test.test",
                "password", "123");

        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(form))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withValidUser_shouldRedirectToUser() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);


        var form = Map.of(
                "username", "test",
                "email", "test@test.test",
                "password", "test");

        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(form))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(302);
    }

}
