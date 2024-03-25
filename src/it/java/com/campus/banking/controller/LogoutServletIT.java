package com.campus.banking.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpRequest.BodyPublishers;

import org.junit.jupiter.api.Test;

import com.campus.banking.AbstractHttpIT;
import com.campus.banking.util.HttpUtils.Response.Status;

public class LogoutServletIT extends AbstractHttpIT {

    private String resource = "/logout";

    @Test
    void post_withLogin_shouldLogout() {
        var client = http.clientBuilder().build();

        var loginRes = http.login(client, "admin", "admin");
        assertThat(loginRes.status()).isEqualTo(Status.Success);
        assertThat(loginRes.httpResponse().statusCode()).isEqualTo(303);

        var request = http.GETRequestBuilder(http.resourceURI(resource))
                .POST(BodyPublishers.noBody()).build();
        var response = http.sendRequest(client, request);
        assertThat(response.status()).isEqualTo(Status.Success);
        assertThat(response.httpResponse().statusCode()).isEqualTo(302);

        var homeReq = http.GETRequestBuilder(http.resourceURI("/"))
                .GET().build();
        var homeRes = http.sendRequest(client, homeReq);
        assertThat(homeRes.status()).isEqualTo(Status.Success);
        assertThat(homeRes.httpResponse().body()).doesNotContainIgnoringCase("account");
    }

}
