package com.campus.banking.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractHttpIT;
import com.campus.banking.util.HttpUtils.Response;

public class UserIdentifierAvailabilityServletIT extends AbstractHttpIT {

    private String resource = "/users/available";

    @Test
    void get_withNoParam_shouldReturnNull() {
        var client = http.clientBuilder().build();
        var request = http.GETRequestBuilder()
                .uri(http.resourceURI(resource))
                .build();
        var response = http.sendRequest(client, request);
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("null");
        assertThat(response.httpResponse().body()).doesNotContain("true","false");
    }

    @Test
    void get_withNonExistingUsername_shouldReturnTrue() {
        var client = http.clientBuilder().build();
        var param = "?username=test";
        var request = http.GETRequestBuilder()
                .uri(http.resourceURI(resource+param))
                .build();
        var response = http.sendRequest(client, request);
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("true","null");
    }

    @Test
    void get_withExistingUsername_shouldReturnFalse() {
        var client = http.clientBuilder().build();
        var param = "?username=admin";
        var request = http.GETRequestBuilder()
                .uri(http.resourceURI(resource+param))
                .build();
        var response = http.sendRequest(client, request);
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("false","null");
    }

    @Test
    void get_withNonExistingEmail_shouldReturnTrue() {
        var client = http.clientBuilder().build();
        var param = "?email=test@test.test";
        var request = http.GETRequestBuilder()
                .uri(http.resourceURI(resource+param))
                .build();
        var response = http.sendRequest(client, request);
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("true","null");
    }

    @Test
    void get_withExistingEmail_shouldReturnFalse() {
        var client = http.clientBuilder().build();
        var param = "?email=admin@bank.co";
        var request = http.GETRequestBuilder()
                .uri(http.resourceURI(resource+param))
                .build();
        var response = http.sendRequest(client, request);
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("false","null");
    }

    @Test
    void get_withExistingUsernameAndNonExistingEmail_shouldReturnFalseAndTrue() {
        var client = http.clientBuilder().build();
        var param = "?username=admin&email=test@test.test";
        var request = http.GETRequestBuilder()
                .uri(http.resourceURI(resource+param))
                .build();
        var response = http.sendRequest(client, request);
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("true","false");
        assertThat(response.httpResponse().body()).doesNotContain("null");
    }

    @Test
    void get_withNoneExistingUsernameAndExistingEmail_shouldReturnFalseAndTrue() {
        var client = http.clientBuilder().build();
        var param = "?username=test&email=admin@bank.co";
        var request = http.GETRequestBuilder()
                .uri(http.resourceURI(resource+param))
                .build();
        var response = http.sendRequest(client, request);
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("true","false");
        assertThat(response.httpResponse().body()).doesNotContain("null");
    }

    @Test
    void get_withExistingUsernameAndExistingEmail_shouldReturnFalseAndTrue() {
        var client = http.clientBuilder().build();
        var param = "?username=admin&email=admin@bank.co";
        var request = http.GETRequestBuilder()
                .uri(http.resourceURI(resource+param))
                .build();
        var response = http.sendRequest(client, request);
        assertThat(response.status()).isEqualTo(Response.Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(200);
        assertThat(response.httpResponse().body()).contains("false");
        assertThat(response.httpResponse().body()).doesNotContain("null","true");
    }

}
