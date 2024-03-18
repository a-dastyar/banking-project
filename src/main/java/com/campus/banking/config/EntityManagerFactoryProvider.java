package com.campus.banking.config;

import java.util.Map;
import org.eclipse.microprofile.config.Config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class EntityManagerFactoryProvider {

    @Inject
    Config config;

    @Inject
    BeanManager beanManager;

    @Produces
    @ApplicationScoped
    public EntityManagerFactory createEntityManagerFactory() {
        log.debug("Creating EntityManagerFactory");

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
                "jakarta.persistence.sql-load-script-source", "scripts/insert_admin.sql",
                "jakarta.persistence.schema-generation.database.action", schema,
                "jakarta.persistence.bean.manager", beanManager);
                
        return Persistence.createEntityManagerFactory("App", properties);
    }

    public void close(@Disposes EntityManagerFactory emf) {
        log.debug("Closing EntityManagerFactory");
        if (emf.isOpen()) {
            emf.close();
        }
    }
}