package com.campus.banking;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.campus.banking.util.HttpUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractHttpIT extends AbstractIT{

    private  ServerManager server = new ServerManager();

    protected  HttpUtils http = new HttpUtils(server.port);

    @BeforeAll
    public static void beforeAll() {
    }
    
    @AfterAll
    public static void afterAll() {
    }
    
    @BeforeEach
    void beforeEach(){
        log.debug("Starting application...");
        setDatabaseConfig();
        server.startServer();
        log.debug("READY");
    }
    
    @AfterEach
    void afterEach(){
        log.debug("Shuting down application...");
        server.stopServer();
    }

    private void setDatabaseConfig() {
        log.debug("Setting database configs");
        log.debug("URL[{}], Username[{}], Password[{}]", mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
        System.setProperty("server.port", String.valueOf(server.port));
        System.setProperty("datasource.url", mysql.getJdbcUrl());
        System.setProperty("datasource.user", mysql.getUsername());
        System.setProperty("datasource.password", mysql.getPassword());
    }

}