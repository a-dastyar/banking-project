package com.campus.banking.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.ConnectException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import com.campus.banking.util.HttpUtils.Response.Status;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtils {

    private final String baseURI;

    private int defaultTimeout = 500;

    public HttpUtils(int port) {
        baseURI = String.format("http://localhost:%d/banking", port);
    }

    public record Response<T>(Response.Status status, HttpResponse<T> httpResponse) {
        public static enum Status {
            Timeout, ConnectionFailed, Success;
        }
    }

    public URI resourceURI(String resource) {
        String uri = String.format("%s%s", baseURI, resource);
        return URI.create(uri);
    }

    public HttpRequest.Builder GETRequestBuilder(URI uri) {
        return HttpRequest.newBuilder(uri)
                .timeout(Duration.ofMillis(defaultTimeout));
    }

    public HttpRequest.Builder GETRequestBuilder() {
        return HttpRequest.newBuilder()
                .timeout(Duration.ofMillis(defaultTimeout));
    }

    public HttpRequest.Builder POSTRequestBuilder() {
        return HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.ofMillis(defaultTimeout));
    }

    public HttpClient.Builder clientBuilder() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager());
    }

    public Response<String> sendRequest(HttpClient client, HttpRequest request) {
        log.debug("Sending {} request to {}", request.method(), request.uri());
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new Response<>(Status.Success, response);
        } catch (HttpTimeoutException e) {
            return new Response<>(Status.Timeout, null);
        } catch (ConnectException e) {
            return new Response<>(Status.ConnectionFailed, null);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Response<String> login(HttpClient client, String username, String password) {
        var req = GETRequestBuilder(resourceURI("/login"))
                .timeout(Duration.ofSeconds(2))
                .build();
        var loginResponse = sendRequest(client, req);
        assertThat(loginResponse.status()).isEqualTo(Response.Status.Success);

        var request = POSTRequestBuilder()
                .uri(resourceURI("/j_security_check"))
                .timeout(Duration.ofSeconds(4))
                .POST(getFormDataBody(Map.of("j_username", username, "j_password", password)))
                .build();
        var response = sendRequest(client, request);
        return response;
    }

    public Response<String> signup(HttpClient client, Map<String, String> form) {
        var request = POSTRequestBuilder()
                .uri(resourceURI("/signup"))
                .timeout(Duration.ofMinutes(2))
                .POST(getFormDataBody(form))
                .build();
        var response = sendRequest(client, request);
        return response;
    }

    public BodyPublisher getFormDataBody(Map<String, String> formData) {
        var body = formData.entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));
        return BodyPublishers.ofString(body);
    }

    public boolean healthCheck(URI uri, int retires) {
        var client = HttpClient.newHttpClient();
        var request = GETRequestBuilder(uri)
                .GET()
                .build();
        var response = sendRequest(client, request);
        return response.status() == Response.Status.Success && response.httpResponse().statusCode() == 200;
    }
}
