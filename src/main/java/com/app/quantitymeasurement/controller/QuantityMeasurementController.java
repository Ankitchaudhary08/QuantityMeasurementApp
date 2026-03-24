package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QuantityMeasurementController — Thin presentation layer that delegates to the
 * service.
 */
public class QuantityMeasurementController {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementController.class);
    private final IQuantityMeasurementService service;

    public QuantityMeasurementController(IQuantityMeasurementService service) {
        this.service = service;
        logger.info("QuantityMeasurementController initialised");
    }

    public void performComparison(QuantityDTO q1, QuantityDTO q2) {
        displayResult(service.compare(q1, q2));
    }

    public void performConversion(QuantityDTO source, QuantityDTO targetUnit) {
        displayResult(service.convert(source, targetUnit));
    }

    public void performAddition(QuantityDTO q1, QuantityDTO q2) {
        displayResult(service.add(q1, q2));
    }

    public void performSubtraction(QuantityDTO q1, QuantityDTO q2) {
        displayResult(service.subtract(q1, q2));
    }

    public void performDivision(QuantityDTO q1, QuantityDTO q2) {
        displayResult(service.divide(q1, q2));
    }

    private void displayResult(QuantityMeasurementEntity entity) {
        if (entity.hasError())
            logger.error("Operation failed: {}", entity.getErrorMessage());
        else
            logger.info("{}", entity);
    }
}
