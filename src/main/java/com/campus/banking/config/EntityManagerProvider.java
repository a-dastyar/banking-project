package com.campus.banking.config;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class EntityManagerProvider {

    @Inject
    EntityManagerFactory factory;

    @Produces
    @RequestScoped
    public EntityManager createEntityManager() {
        log.debug("Creating EntityManager");
        return factory.createEntityManager();
    }

    public void close(@Disposes EntityManager em) {
        log.debug("Closing EntityManager");
        if (em.isOpen()) {
            em.close();
        }
    }

}
