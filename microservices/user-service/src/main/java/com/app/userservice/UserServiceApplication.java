package com.app.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UserServiceApplication — Microservice responsible for:
 *   - User registration (local email/password)
 *   - User login with JWT issuance
 *   - User profile management
 *   - Runs on port 8081
 */
@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
