package com.app.measurementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MeasurementServiceApplication — Microservice responsible for:
 *   - Core quantity operations: compare, convert, add, subtract, divide
 *   - Persists each operation result to its own H2 (dev) / MySQL (prod) database
 *   - Publishes operation events to history-service via REST (or messaging)
 *   - Runs on port 8082
 */
@SpringBootApplication
public class MeasurementServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeasurementServiceApplication.class, args);
    }
}
