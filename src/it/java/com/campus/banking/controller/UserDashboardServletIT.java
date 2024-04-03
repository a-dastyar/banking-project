package com.campus.banking.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractHttpIT;
import com.campus.banking.util.HttpUtils.Response;
import com.campus.banking.util.HttpUtils.Response.Status;

public class UserDashboardServletIT extends AbstractHttpIT {

    private String resource = "/dashboard";

    private String accountResource = "/bank-accounts";

    @Test
    void get_withoutLogin_shouldReturn401() {
        var client = http.clientBuilder().build();
        var request = http.GETRequestBuilder(http.resourceURI(resource)).build();
        var response = http.sendRequest(client, request);
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(401);
    }

    @Test
    void get_withLoginNoAccount_shouldReturnNoAccount() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var request = http.GETRequestBuilder(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(2))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
    }

    @Test
    void get_withLoginAndAccount_shouldReturnAccount() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
                "username", "admin",
                "balance", "565.0");

        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);

        var request = http.GETRequestBuilder(http.resourceURI(resource)).build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("565");
    }

    @Test
    void post_withSameEmail_shouldRedirect() {
        var client = http.clientBuilder()
                .followRedirects(Redirect.ALWAYS).build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(200);

        var user = Map.of(
                "email", "admin@bank.co");
        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(2))
                .POST(http.getFormDataBody(user))
                .build();

        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("admin@bank.co");
    }

    @Test
    void post_withExistingEmail_shouldReturnFail() {
        var client = http.clientBuilder().build();

        var form = Map.of(
                "username", "test",
                "email", "test@test.test",
                "password", "test");
        var signupRes = http.signup(client, form);
        assertThat(signupRes.status()).isEqualTo(Status.Success);
        assertThat(signupRes.httpResponse().statusCode()).isEqualTo(302);

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var user = Map.of(
                "email", "test@test.test");
        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(2))
                .POST(http.getFormDataBody(user))
                .build();

        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withNewEmail_shouldUpdate() {
        var client = http.clientBuilder()
                .followRedirects(Redirect.ALWAYS).build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(200);

        var user = Map.of(
                "email", "admin@bank.com");
        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(2))
                .POST(http.getFormDataBody(user))
                .build();

        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("admin@bank.com");

    }

    private Response<String> addAccount(HttpClient client, Map<String, String> account) {
        var addRequest = http.POSTRequestBuilder()
                .uri(http.resourceURI(accountResource))
                .timeout(Duration.ofSeconds(2))
                .POST(http.getFormDataBody(account))
                .build();
        var addResponse = http.sendRequest(client, addRequest);
        return addResponse;
    }
}
