package com.app.measurementservice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * QuantityInputDTO — Request body wrapping two QuantityDTO operands.
 */
@Data
public class QuantityInputDTO {

    @Valid @NotNull(message = "thisQuantityDTO is required")
    private QuantityDTO thisQuantityDTO;

    @Valid @NotNull(message = "thatQuantityDTO is required")
    private QuantityDTO thatQuantityDTO;
}
