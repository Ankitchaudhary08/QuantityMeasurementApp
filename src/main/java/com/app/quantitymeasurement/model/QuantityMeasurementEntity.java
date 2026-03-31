package com.app.quantitymeasurement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA Entity representing a persisted quantity measurement operation.
 * Maps to the QUANTITY_MEASUREMENT table in the database.
 */
@Entity
@Table(name = "quantity_measurement", indexes = {
        @Index(name = "idx_operation", columnList = "operation"),
        @Index(name = "idx_this_measurement_type", columnList = "this_measurement_type"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_is_error", columnList = "is_error")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---- Operand 1 ----
    @Column(name = "this_value", nullable = false)
    private double thisValue;

    @Column(name = "this_unit", nullable = false)
    private String thisUnit;

    @Column(name = "this_measurement_type", nullable = false)
    private String thisMeasurementType;

    // ---- Operand 2 ----
    @Column(name = "that_value", nullable = false)
    private double thatValue;

    @Column(name = "that_unit", nullable = false)
    private String thatUnit;

    @Column(name = "that_measurement_type", nullable = false)
    private String thatMeasurementType;

    // ---- Operation ----
    @Column(name = "operation", nullable = false)
    private String operation;

    // ---- Result ----
    @Column(name = "result_string")
    private String resultString;

    @Column(name = "result_value")
    private double resultValue;

    @Column(name = "result_unit")
    private String resultUnit;

    @Column(name = "result_measurement_type")
    private String resultMeasurementType;

    // ---- Error info ----
    @Column(name = "error_message", length = 1024)
    private String errorMessage;

    @Column(name = "is_error", nullable = false)
    private boolean error;

    // ---- Timestamps ----
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
