package com.app.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway — Single entry point for all Quantity Measurement microservices.
 *
 * Routes:
 *   /api/v1/auth/**        -> user-service:8081
 *   /api/v1/users/**       -> user-service:8081
 *   /api/v1/quantities/**  -> measurement-service:8082
 *   /api/v1/history/**     -> history-service:8083
 *
 * Validates JWT on protected routes before forwarding.
 */
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
