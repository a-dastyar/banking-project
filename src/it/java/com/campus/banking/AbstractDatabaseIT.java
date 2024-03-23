package com.campus.banking;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDatabaseIT extends AbstractIT {

    protected EntityManagerFactory emf;

    @BeforeEach
    public void beforeEach() {
        log.debug("Creating EntityManagerFactory");
        var showSql = false;
        var properties = Map.of(
                "hibernate.hikari.jdbcUrl", mysql.getJdbcUrl(),
                "hibernate.hikari.dataSource.user", mysql.getUsername(),
                "hibernate.hikari.dataSource.password", mysql.getPassword(),
                "hibernate.show_sql", showSql,
                "hibernate.format_sql", showSql,
                "hibernate.highlight_sql", showSql,
                "jakarta.persistence.schema-generation.database.action", "drop-and-create");

        emf = Persistence.createEntityManagerFactory("App", properties);
    }

    @AfterEach
    public void afterEach() {
        log.debug("Closing EntityManagerFactory");
        emf.close();
    }
}
