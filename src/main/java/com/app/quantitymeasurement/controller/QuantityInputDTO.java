package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.model.QuantityDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QuantityInputDTO — Wraps two QuantityDTO objects as input for binary operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityInputDTO {

    @NotNull(message = "thisQuantityDTO must not be null")
    @Valid
    private QuantityDTO thisQuantityDTO;

    @NotNull(message = "thatQuantityDTO must not be null")
    @Valid
    private QuantityDTO thatQuantityDTO;
}
