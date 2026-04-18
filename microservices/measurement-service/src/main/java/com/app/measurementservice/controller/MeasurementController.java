package com.app.measurementservice.controller;

import com.app.measurementservice.model.MeasurementDTO;
import com.app.measurementservice.model.QuantityInputDTO;
import com.app.measurementservice.service.MeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * MeasurementController — REST API for all quantity operations.
 * Base path: /api/v1/quantities
 *
 * The gateway injects X-Auth-User-Email header for audit tracking.
 */
@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements", description = "Core quantity operations: compare, convert, add, subtract, divide")
public class MeasurementController {

    private static final Logger logger = LoggerFactory.getLogger(MeasurementController.class);

    @Autowired
    private MeasurementService service;

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities",
               description = "Returns true if both quantities are equal after unit conversion")
    public ResponseEntity<MeasurementDTO> compare(
            @Valid @RequestBody QuantityInputDTO input,
            @RequestHeader(value = "X-Auth-User-Email", defaultValue = "anonymous") String userEmail) {
        logger.info("POST /quantities/compare by {}", userEmail);
        return ResponseEntity.ok(service.compare(input.getThisQuantityDTO(), input.getThatQuantityDTO(), userEmail));
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert a quantity to a target unit",
               description = "Converts thisQuantityDTO to the unit specified in thatQuantityDTO")
    public ResponseEntity<MeasurementDTO> convert(
            @Valid @RequestBody QuantityInputDTO input,
            @RequestHeader(value = "X-Auth-User-Email", defaultValue = "anonymous") String userEmail) {
        logger.info("POST /quantities/convert by {}", userEmail);
        return ResponseEntity.ok(service.convert(input.getThisQuantityDTO(), input.getThatQuantityDTO(), userEmail));
    }

    @PostMapping("/add")
    @Operation(summary = "Add two quantities",
               description = "Adds two quantities of the same measurement type")
    public ResponseEntity<MeasurementDTO> add(
            @Valid @RequestBody QuantityInputDTO input,
            @RequestHeader(value = "X-Auth-User-Email", defaultValue = "anonymous") String userEmail) {
        logger.info("POST /quantities/add by {}", userEmail);
        return ResponseEntity.ok(service.add(input.getThisQuantityDTO(), input.getThatQuantityDTO(), userEmail));
    }

    @PostMapping("/subtract")
    @Operation(summary = "Subtract two quantities",
               description = "Subtracts thatQuantityDTO from thisQuantityDTO")
    public ResponseEntity<MeasurementDTO> subtract(
            @Valid @RequestBody QuantityInputDTO input,
            @RequestHeader(value = "X-Auth-User-Email", defaultValue = "anonymous") String userEmail) {
        logger.info("POST /quantities/subtract by {}", userEmail);
        return ResponseEntity.ok(service.subtract(input.getThisQuantityDTO(), input.getThatQuantityDTO(), userEmail));
    }

    @PostMapping("/divide")
    @Operation(summary = "Divide two quantities",
               description = "Divides thisQuantityDTO by thatQuantityDTO")
    public ResponseEntity<MeasurementDTO> divide(
            @Valid @RequestBody QuantityInputDTO input,
            @RequestHeader(value = "X-Auth-User-Email", defaultValue = "anonymous") String userEmail) {
        logger.info("POST /quantities/divide by {}", userEmail);
        return ResponseEntity.ok(service.divide(input.getThisQuantityDTO(), input.getThatQuantityDTO(), userEmail));
    }
}
