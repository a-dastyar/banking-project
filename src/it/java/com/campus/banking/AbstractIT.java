package com.campus.banking;

import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Testcontainers
public abstract class AbstractIT {

    static String DB_IMAGE = "mysql:8.0.36-bookworm";

    protected EntityManagerFactory emf;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(DB_IMAGE);

    @BeforeEach
    public void beforeEach() {
        log.debug("Creating EntityManagerFactory");
        var properties = Map.of(
                "hibernate.hikari.jdbcUrl", mysql.getJdbcUrl(),
                "hibernate.hikari.dataSource.user", mysql.getUsername(),
                "hibernate.hikari.dataSource.password", mysql.getPassword(),
                "hibernate.show_sql", "false",
                "jakarta.persistence.schema-generation.database.action", "create-drop");

        emf = Persistence.createEntityManagerFactory("App", properties);
    }

    @AfterEach
    public void afterEach() {
        log.debug("Closing EntityManagerFactory");
        emf.close();
    }
}