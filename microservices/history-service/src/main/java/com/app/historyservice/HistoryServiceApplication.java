package com.app.historyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HistoryServiceApplication — Microservice responsible for:
 *   - Providing history of all quantity operations
 *   - Filtering history by user, operation type, or measurement type
 *   - Aggregating operation counts
 *   - Runs on port 8083
 */
@SpringBootApplication
public class HistoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HistoryServiceApplication.class, args);
    }
}
