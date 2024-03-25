package com.campus.banking.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractHttpIT;
import com.campus.banking.util.HttpUtils.Response;

public class HomeServletIT extends AbstractHttpIT {

    private String resource = "/";

    @Test
    void get_withoutLogin_shouldNotContainAccount() {
        var client = http.clientBuilder().build();

        var request = http.GETRequestBuilder(http.resourceURI(resource)).GET().build();
        var response = http.sendRequest(client, request);

        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).doesNotContainIgnoringCase("account");
    }

    @Test
    void get_withLogin_shouldContainAccount() {
        var client = http.clientBuilder().build();

        var loginResponse = http.login(client, "admin", "admin");
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);
        assertThat(loginResponse.httpResponse().statusCode()).isEqualTo(303);

        var request = http.GETRequestBuilder(http.resourceURI(resource)).build();
        var response = http.sendRequest(client, request);
        
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).containsIgnoringCase("account");
    }

}
