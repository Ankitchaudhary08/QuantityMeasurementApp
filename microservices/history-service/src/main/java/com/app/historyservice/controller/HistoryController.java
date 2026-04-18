package com.app.historyservice.controller;

import com.app.historyservice.model.HistoryRecord;
import com.app.historyservice.repository.HistoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/history")
@Tag(name = "Operation History", description = "Query history of quantity measurement operations")
public class HistoryController {

    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);

    @Autowired
    private HistoryRepository repository;

    @GetMapping
    @Operation(summary = "Get all history", description = "Returns history of all operations")
    public ResponseEntity<List<HistoryRecord>> getAllHistory() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/user")
    @Operation(summary = "Get current user history", description = "Returns history for the authenticated user")
    public ResponseEntity<List<HistoryRecord>> getMyHistory(@RequestHeader("X-Auth-User-Email") String email) {
        logger.info("Fetching history for user: {}", email);
        return ResponseEntity.ok(repository.findByPerformedBy(email));
    }

    @GetMapping("/operation/{operation}")
    @Operation(summary = "Get history by operation type")
    public ResponseEntity<List<HistoryRecord>> getHistoryByOperation(@PathVariable String operation) {
        return ResponseEntity.ok(repository.findByOperation(operation.toLowerCase()));
    }

    @GetMapping("/type/{measurementType}")
    @Operation(summary = "Get history by measurement type")
    public ResponseEntity<List<HistoryRecord>> getHistoryByType(@PathVariable String measurementType) {
        return ResponseEntity.ok(repository.findByThisMeasurementType(measurementType));
    }

    @GetMapping("/errors")
    @Operation(summary = "Get all errored operations")
    public ResponseEntity<List<HistoryRecord>> getErrorHistory() {
        return ResponseEntity.ok(repository.findByErrorTrue());
    }

    @GetMapping("/count/{operation}")
    @Operation(summary = "Get successful operation count")
    public ResponseEntity<Long> getOperationCount(@PathVariable String operation) {
        return ResponseEntity.ok(repository.countByOperationAndErrorFalse(operation.toLowerCase()));
    }
}
