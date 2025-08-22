package com.sprints.room_booking_system.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class SimpleControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void testWebApplicationContextLoads() {
        // Verify that the web application context loads successfully
        // This includes all controllers, services, and repositories
        assertThat(webApplicationContext).isNotNull();
    }

    @Test
    void testControllersExist() {
        // Verify that REST controllers are properly registered in the context
        assertThat(webApplicationContext.getBeansWithAnnotation(org.springframework.web.bind.annotation.RestController.class))
                .isNotEmpty();
    }
}
