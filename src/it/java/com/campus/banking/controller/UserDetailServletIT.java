package com.campus.banking.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractHttpIT;
import com.campus.banking.util.HttpUtils.Response;
import com.campus.banking.util.HttpUtils.Response.Status;

public class UserDetailServletIT extends AbstractHttpIT {

    private String resource = "/users/details";

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

        var account = Map.of(
                "username", "admin",
                "account_number", "4000-admin",
                "balance", "100.0");
        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);

        var request = http.GETRequestBuilder()
                .uri(http.resourceURI(resource + "?username=admin"))
                .timeout(Duration.ofSeconds(3))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("4000-admin", "admin");
    }

    @Test
    void post_withoutLogin_shouldReturn401() {
        var client = http.clientBuilder().build();
        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(BodyPublishers.noBody()).build();
        var response = http.sendRequest(client, request);
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(401);
    }

    @Test
    void post_withLoginAsMember_shouldReturn401() {
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

        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(BodyPublishers.noBody()).build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(403);
    }

    @Test
    void post_withInvalidUsername_shouldReturnPage() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
                "username", "admin",
                "account_number", "4000-admin",
                "balance", "100.0");
        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);

        var user = Map.of(
                "username", "a",
                "email", "admin@bank.co",
                "roles", "ADMIN");
        var request = http.POSTRequestBuilder()
                .POST(http.getFormDataBody(user))
                .uri(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(3))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withInvalidEmail_shouldReturnPage() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
                "username", "admin",
                "account_number", "4000-admin",
                "balance", "100.0");
        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);

        var user = Map.of(
                "username", "admin",
                "email", "admin@bank",
                "roles", "ADMIN");
        var request = http.POSTRequestBuilder()
                .POST(http.getFormDataBody(user))
                .uri(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(3))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withNoRole_shouldReturnPage() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
                "username", "admin",
                "account_number", "4000-admin",
                "balance", "100.0");
        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);

        var user = Map.of(
                "username", "admin",
                "email", "admin@bank");
        var request = http.POSTRequestBuilder()
                .POST(http.getFormDataBody(user))
                .uri(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(3))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withLoginAsAdmin_shouldReturnPage() {
        var client = http.clientBuilder()
                .followRedirects(Redirect.ALWAYS).build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(200);

        var account = Map.of(
                "username", "admin",
                "account_number", "4000-admin",
                "balance", "100.0");
        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(200);

        var user = Map.of(
                "username", "admin",
                "email", "admin-new@bank.co",
                "roles", "ADMIN");
        var request = http.POSTRequestBuilder()
                .POST(http.getFormDataBody(user))
                .uri(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(3))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("admin-new");
    }

    private Response<String> addAccount(HttpClient client, Map<String, String> account) {
        var addRequest = http.POSTRequestBuilder()
                .uri(http.resourceURI("/bank-accounts"))
                .timeout(Duration.ofSeconds(2))
                .POST(http.getFormDataBody(account))
                .build();
        var addResponse = http.sendRequest(client, addRequest);
        return addResponse;
    }

}
