package com.ef.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

@ApplicationScoped
public class EMFactory {

    @Produces
    public EntityManager createEntityManager() {
        return Persistence.createEntityManagerFactory("parserPU").createEntityManager();
    }

    public void closeEM(@Disposes EntityManager manager) {
        manager.close();
    }
}
