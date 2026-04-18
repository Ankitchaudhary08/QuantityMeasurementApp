package com.app.measurementservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * QuantityDTO — Represents a single quantity (value + unit + type).
 */
@Data
public class QuantityDTO {

    private double value;

    @NotBlank(message = "unit is required")
    private String unit;

    @NotBlank(message = "measurementType is required")
    private String measurementType;
}
