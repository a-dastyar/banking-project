package com.campus.banking.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpClient.Redirect;

import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractHttpIT;
import com.campus.banking.util.HttpUtils.Response;

public class LoginServletIT extends AbstractHttpIT {

    private String resource = "/login";

    @Test
    void get_withoutLogin_shouldReturnLoginForm() {
        var client = http.clientBuilder().build();

        var request = http.GETRequestBuilder(http.resourceURI(resource)).GET().build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(401);
        assertThat(response.httpResponse().body()).containsIgnoringCase("j_username");
    }

    @Test
    void get_withLogin_shouldRedirectToHome() {
        var client = http.clientBuilder()
                .followRedirects(Redirect.ALWAYS).build();

        var response = http.login(client, "admin", "admin");

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).containsIgnoringCase("account");
    }

    @Test
    void get_withInvalidCredentials_shouldRedirectToLogin() {
        var client = http.clientBuilder()
                .followRedirects(Redirect.ALWAYS).build();

        var response = http.login(client, "test", "test");
        
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().body()).containsIgnoringCase("Invalid");
    }

}
