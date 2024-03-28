package com.campus.banking.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractHttpIT;
import com.campus.banking.util.HttpUtils.Response;
import com.campus.banking.util.HttpUtils.Response.Status;

public class CheckingAccountBalanceServletIT extends AbstractHttpIT {

    private String resource = "/checking-accounts/balance";

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
    void post_withoutAmount_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var balanceChange = Map.of(
                "type", "WITHDRAW");
        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(2))
                .POST(http.getFormDataBody(balanceChange))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withoutType_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var balanceChange = Map.of(
                "amount", "300.0");
        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .timeout(Duration.ofSeconds(2))
                .POST(http.getFormDataBody(balanceChange))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withWithdrawMoreThanBalance_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
                "username", "admin",
                "balance", "500.0",
                "overdraft_limit", "0",
                "debt", "0.0");
        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);
        var accountNumber = addResponse.httpResponse().headers().firstValue("location")
                .map(str -> str.split("=")[1]).get();

        var balanceChange = Map.of(
                "account_number", accountNumber,
                "amount", "600.0",
                "type", "WITHDRAW");
        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(balanceChange))
                .timeout(Duration.ofSeconds(2))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withWithdrawMoreThanBalanceAndOverdraft_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
                "username", "admin",
                "balance", "500.0",
                "overdraft_limit", "400",
                "debt", "0.0");
        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);
        var accountNumber = addResponse.httpResponse().headers().firstValue("location")
                .map(str -> str.split("=")[1]).get();

        var balanceChange = Map.of(
                "account_number", accountNumber,
                "amount", "1000.0",
                "type", "WITHDRAW");
        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(balanceChange))
                .timeout(Duration.ofSeconds(2))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withWithdraw_shouldRedirectToAccount() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
                "username", "admin",
                "balance", "500.0",
                "overdraft_limit", "400",
                "debt", "0.0");
        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);
        var accountNumber = addResponse.httpResponse().headers().firstValue("location")
                .map(str -> str.split("=")[1]).get();

        var balanceChange = Map.of(
                "account_number", accountNumber,
                "amount", "150.0",
                "type", "WITHDRAW");
        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(balanceChange))
                .timeout(Duration.ofSeconds(2))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(302);
    }

    @Test
    void post_withDepositLessThanMinDeposit_shouldReturn400() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
                "username", "admin",
                "balance", "500.0",
                "overdraft_limit", "400",
                "debt", "0.0");
        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);
        var accountNumber = addResponse.httpResponse().headers().firstValue("location")
                .map(str -> str.split("=")[1]).get();

        var balanceChange = Map.of(
                "account_number", accountNumber,
                "amount", "0.0",
                "type", "DEPOSIT");
        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(balanceChange))
                .timeout(Duration.ofSeconds(2))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(400);
    }

    @Test
    void post_withDeposit_shouldRedirectToAccount() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var account = Map.of(
                "username", "admin",
                "balance", "500.0",
                "overdraft_limit", "400",
                "debt", "0.0");
        var addResponse = addAccount(client, account);
        assertThat(addResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(addResponse.httpResponse().statusCode()).isEqualTo(302);
        var accountNumber = addResponse.httpResponse().headers().firstValue("location")
                .map(str -> str.split("=")[1]).get();

        var balanceChange = Map.of(
                "account_number", accountNumber,
                "amount", "250.0",
                "type", "DEPOSIT");
        var request = http.POSTRequestBuilder()
                .uri(http.resourceURI(resource))
                .POST(http.getFormDataBody(balanceChange))
                .timeout(Duration.ofSeconds(2))
                .build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(302);
    }

    private Response<String> addAccount(HttpClient client, Map<String, String> account) {
        var addRequest = http.POSTRequestBuilder()
                .uri(http.resourceURI("/checking-accounts"))
                .timeout(Duration.ofSeconds(2))
                .POST(http.getFormDataBody(account))
                .build();
        var addResponse = http.sendRequest(client, addRequest);
        return addResponse;
    }

}