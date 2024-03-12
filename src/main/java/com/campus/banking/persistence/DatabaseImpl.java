package com.campus.banking.persistence;

import java.util.Map;
import java.util.function.Function;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public enum DatabaseImpl implements Database {
    INSTANCE;

    private Config config = ConfigProvider.getConfig();
    private EntityManagerFactory factory = createEntityManagerFactory();

    private EntityManagerFactory createEntityManagerFactory() {
        String url = config.getValue("datasource.url", String.class);
        String username = config.getValue("datasource.user", String.class);
        String password = config.getValue("datasource.password", String.class);
        String schema = config.getValue("datasource.schema.generation.strategy", String.class);
        String showSQL = config.getValue("datasource.show_sql", String.class);

        var properties = Map.of(
                "hibernate.hikari.jdbcUrl", url,
                "hibernate.hikari.dataSource.user", username,
                "hibernate.hikari.dataSource.password", password,
                "hibernate.show_sql", showSQL,
                "hibernate.format_sql", showSQL,
                "hibernate.highlight_sql", showSQL,
                "jakarta.persistence.schema-generation.database.action", schema);

        return Persistence.createEntityManagerFactory("App", properties);
    }

    @Override
    public <U> U withEntityManager(Function<EntityManager, U> action) {
        try (var em = factory.createEntityManager()) {
            return action.apply(em);
        }
    }

    @Override
    public void closeEntityManagerFactory() {
        factory.close();
    }

}