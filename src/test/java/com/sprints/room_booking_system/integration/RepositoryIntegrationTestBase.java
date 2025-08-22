package com.sprints.room_booking_system.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public abstract class RepositoryIntegrationTestBase {

    @Autowired
    protected TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear all data before each test
        entityManager.clear();
    }

    protected <T> T persistAndFlush(T entity) {
        T savedEntity = entityManager.persist(entity);
        entityManager.flush();
        return savedEntity;
    }

    protected <T> T find(Class<T> entityClass, Object id) {
        return entityManager.find(entityClass, id);
    }

    protected void remove(Object entity) {
        entityManager.remove(entity);
        entityManager.flush();
    }
}
