package com.app.quantitymeasurement.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * QuantityDTO — Data Transfer Object for a single quantity (value + unit + type).
 * Used as input to REST API operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityDTO {

    @NotNull(message = "Value must not be null")
    private Double value;

    @NotEmpty(message = "Unit must not be empty")
    private String unit;

    @NotEmpty(message = "Measurement type must not be empty")
    private String measurementType;
}
