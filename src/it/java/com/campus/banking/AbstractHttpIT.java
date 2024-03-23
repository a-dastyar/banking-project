package com.campus.banking;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.campus.banking.util.HttpUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractHttpIT extends AbstractIT {

    private static ServerManager server = new ServerManager();

    protected static HttpUtils http = new HttpUtils(server.port);

    @BeforeAll
    public static void beforeAll() {
        log.debug("Starting application...");
        setDatabaseConfig();
        server.startServer();
        log.debug("READY");
    }

    @AfterAll
    public static void afterAll() {
        log.debug("Shuting down application...");
        server.stopServer();
    }

    private static void setDatabaseConfig() {
        log.debug("Setting database configs");
        log.debug("URL[{}], Username[{}], Password[{}]", mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
        System.setProperty("server.port", String.valueOf(server.port));
        System.setProperty("datasource.url", mysql.getJdbcUrl());
        System.setProperty("datasource.user", mysql.getUsername());
        System.setProperty("datasource.password", mysql.getPassword());
    }

}