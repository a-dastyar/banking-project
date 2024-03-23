package com.campus.banking;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.stream.IntStream;

import org.eclipse.microprofile.config.ConfigProvider;

import com.campus.banking.app.Server;
import com.campus.banking.app.ServerFailureException;
import com.campus.banking.app.TomcatEmbedded;
import com.campus.banking.util.HttpUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerManager {

    private Server server;

    public final int port = 8089;

    private HttpUtils http = new HttpUtils(port);

    private int reties = 50;

    private Duration waitPerTry = Duration.ofSeconds(1);

    public void startServer() {
        var tomcat = new TomcatEmbedded(ConfigProvider.getConfig());
        Thread.ofVirtual().start(() -> {
            try {
                tomcat.start();
            } catch (ServerFailureException e) {
                throw new RuntimeException(e);
            }
        });
        var started = IntStream.range(0, reties)
                .anyMatch(this::healthCheck);
        if (!started) {
            fail("Failed to start the server: timeout");
        }
        server = tomcat;
        log.debug("Server started successfully");
    }

    public void stopServer() {
        try {
            server.stop();
            var running = IntStream.range(0, reties)
                    .allMatch(this::healthCheck);
            if (running) {
                fail("Failed to stop the server: timeout");
            }
            log.debug("Server stopped successfully");
        } catch (ServerFailureException e) {
            e.printStackTrace();
            fail("Failed to stop the server");
        }
    }

    private boolean healthCheck(int i) {
        sleep(waitPerTry);
        return http.healthCheck(http.resourceURI("/"), i);
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
