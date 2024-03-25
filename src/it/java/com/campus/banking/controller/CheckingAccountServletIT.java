package com.campus.banking.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractHttpIT;
import com.campus.banking.util.HttpUtils.Response;
import com.campus.banking.util.HttpUtils.Response.Status;

public class CheckingAccountServletIT extends AbstractHttpIT {

    private String resource = "/checking-accounts";

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
        assertThat(response.httpResponse().body()).containsIgnoringCase("Sum");
    }

    @Test
    void get_withSumTwoMatchingAccounts_shouldReturnSum() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var request = http.GETRequestBuilder()
                .uri(http.resourceURI("/checking-accounts?sum_min=550"))
                .timeout(Duration.ofSeconds(2))
                .build();

        var firstAccount = Map.of(
                "username", "admin",
                "account_number", "4000-admin",
                "balance", "500.0",
                "overdraft_limit", "100.0",
                "debt", "0.0");

        var secondAccount = Map.of(
                "username", "admin",
                "account_number", "5000-admin",
                "balance", "700.0",
                "overdraft_limit", "100.0",
                "debt", "0.0");

        var thirdAccount = Map.of(
                "username", "admin",
                "account_number", "6000-admin",
                "balance", "700.0",
                "overdraft_limit", "100.0",
                "debt", "0.0");

        var addResponse = addAccount(client, firstAccount);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);

        addResponse = addAccount(client, secondAccount);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);

        addResponse = addAccount(client, thirdAccount);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);

        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).containsIgnoringCase("1200.0");
    }

    private Response<String> addAccount(HttpClient client, Map<String, String> account) {
        var addRequest = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(2))
                .POST(http.getFormDataBody(account))
                .build();
        var addResponse = http.sendRequest(client, addRequest);
        return addResponse;
    }

    @Test
    void post_withoutLogin_shouldReturn401() {
        var client = http.clientBuilder().build();

        var account = Map.of(
                "username", "admin",
                "account_number", "4000-admin",
                "balance", "500.0",
                "overdraft_limit", "100.0",
                "debt", "0.0");

        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(account))
                .build();
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

        var account = Map.of(
            "username", "admin",
            "account_number", "4000-admin",
            "balance", "500.0",
            "overdraft_limit", "100.0",
            "debt", "0.0");

        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(account))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(403);
    }

    @Test
    void post_withLoginAsAdmin_shouldReturnPage() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
            "username", "admin",
            "account_number", "4000-admin",
            "balance", "500.0",
            "overdraft_limit", "100.0",
            "debt", "0.0");

        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);

        var get = http.GETRequestBuilder()
                .uri(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(2))
                .build();
        var getResponse = http.sendRequest(client, get);

        assertThat(getResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(getResponse.httpResponse().statusCode()).isEqualTo(200);
        assertThat(getResponse.httpResponse().body()).containsIgnoringCase("4000-admin");
    }

    @Test
    void post_withInvalidBalance_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
            "username", "admin",
            "account_number", "4000-admin",
            "balance", "-1.0",
            "overdraft_limit", "100.0",
            "debt", "0.0");

        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withNullAccountNumber_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
            "username", "admin",
            "balance", "500.0",
            "overdraft_limit", "100.0",
            "debt", "0.0");

        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withInvalidOverdraftLimit_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
            "username", "admin",
            "account_number", "4000-admin",
            "balance", "500.0",
            "overdraft_limit", "-1.0",
            "debt", "0.0");

        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withBalanceWhileInDebt_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
            "username", "admin",
            "account_number", "4000-admin",
            "balance", "500.0",
            "overdraft_limit", "100.0",
            "debt", "10.0");

        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withInvalidDebt_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account =  Map.of(
            "username", "admin",
            "account_number", "4000-admin",
            "balance", "0.0",
            "overdraft_limit", "100.0",
            "debt", "-1.0");

        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withDebtHigherThanOverdraftLimit_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account =  Map.of(
            "username", "admin",
            "account_number", "4000-admin",
            "balance", "0.0",
            "overdraft_limit", "200.0",
            "debt", "250.0");

        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withNonExistingUser_shouldReturn404() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
                "username", "test",
                "account_number", "4000-admin",
                "balance", "500.0",
                "overdraft_limit", "200.0",
                "debt", "0.0");

        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(404);
    }

}
