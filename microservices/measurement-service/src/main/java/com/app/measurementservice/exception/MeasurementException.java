package com.app.measurementservice.exception;

/**
 * Thrown when a quantity measurement operation fails due to invalid input.
 */
public class MeasurementException extends RuntimeException {
    public MeasurementException(String message) {
        super(message);
    }
    public MeasurementException(String message, Throwable cause) {
        super(message, cause);
    }
}
