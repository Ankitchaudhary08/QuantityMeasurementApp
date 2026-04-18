package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * QuantityMeasurementController — REST Controller exposing quantity operations.
 * Base path: /api/v1/quantities
 */
@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements", description = "REST API for quantity measurement operations")
public class QuantityMeasurementController {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementController.class);

    @Autowired
    private IQuantityMeasurementService service;

    // ----------------------------------------------------------------
    // Operation Endpoints
    // ----------------------------------------------------------------

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities", description = "Returns true if both quantities are equal after unit conversion")
    public ResponseEntity<QuantityMeasurementDTO> compareQuantities(@Valid @RequestBody QuantityInputDTO input) {
        logger.info("POST /compare called");
        QuantityMeasurementDTO result = service.compare(input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert a quantity to a target unit", description = "Converts thisQuantityDTO to the unit specified in thatQuantityDTO")
    public ResponseEntity<QuantityMeasurementDTO> convertQuantity(@Valid @RequestBody QuantityInputDTO input) {
        logger.info("POST /convert called");
        QuantityMeasurementDTO result = service.convert(input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    @Operation(summary = "Add two quantities", description = "Adds two quantities of the same measurement type")
    public ResponseEntity<QuantityMeasurementDTO> addQuantities(@Valid @RequestBody QuantityInputDTO input) {
        logger.info("POST /add called");
        QuantityMeasurementDTO result = service.add(input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/subtract")
    @Operation(summary = "Subtract two quantities", description = "Subtracts thatQuantityDTO from thisQuantityDTO")
    public ResponseEntity<QuantityMeasurementDTO> subtractQuantities(@Valid @RequestBody QuantityInputDTO input) {
        logger.info("POST /subtract called");
        QuantityMeasurementDTO result = service.subtract(input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/divide")
    @Operation(summary = "Divide two quantities", description = "Divides thisQuantityDTO by thatQuantityDTO (same type required)")
    public ResponseEntity<QuantityMeasurementDTO> divideQuantities(@Valid @RequestBody QuantityInputDTO input) {
        logger.info("POST /divide called");
        QuantityMeasurementDTO result = service.divide(input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    // ----------------------------------------------------------------
    // History / Query Endpoints
    // ----------------------------------------------------------------

    @GetMapping("/history/operation/{operation}")
    @Operation(summary = "Get history by operation type", description = "Returns all records for a given operation (compare, add, convert, etc.)")
    public ResponseEntity<List<QuantityMeasurementDTO>> getHistoryByOperation(@PathVariable String operation) {
        logger.info("GET /history/operation/{}", operation);
        return ResponseEntity.ok(service.getHistoryByOperation(operation));
    }

    @GetMapping("/history/type/{measurementType}")
    @Operation(summary = "Get history by measurement type", description = "Returns all records for a given measurement type (LengthUnit, WeightUnit, etc.)")
    public ResponseEntity<List<QuantityMeasurementDTO>> getHistoryByType(@PathVariable String measurementType) {
        logger.info("GET /history/type/{}", measurementType);
        return ResponseEntity.ok(service.getHistoryByMeasurementType(measurementType));
    }

    @GetMapping("/count/{operation}")
    @Operation(summary = "Get count of successful operations", description = "Returns count of successful operations for a given type")
    public ResponseEntity<Long> getOperationCount(@PathVariable String operation) {
        logger.info("GET /count/{}", operation);
        return ResponseEntity.ok(service.getOperationCount(operation));
    }

    @GetMapping("/history/errored")
    @Operation(summary = "Get all errored operations", description = "Returns all records where an error occurred")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErrorHistory() {
        logger.info("GET /history/errored");
        return ResponseEntity.ok(service.getErrorHistory());
    }
}
