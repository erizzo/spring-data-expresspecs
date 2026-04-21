package rizzoweb.spring.jpa;

import org.springframework.beans.factory.annotation.Autowired;

import rizzoweb.spring.jpa.specifications.EntityManagerWrapper;

public class BaseJPAIntegrationTest {

    @Autowired
    protected EntityManagerWrapper entityManager;


    protected <T> T persistAndFlush(T entity) {
        return entityManager.persistAndFlush(entity);
    }

}