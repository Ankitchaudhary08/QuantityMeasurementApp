package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementDTO;

import java.util.List;

/**
 * IQuantityMeasurementService — Service layer interface for quantity operations (UC17).
 * Methods return QuantityMeasurementDTO for structured API responses.
 */
public interface IQuantityMeasurementService {

    QuantityMeasurementDTO compare(QuantityDTO q1, QuantityDTO q2);

    QuantityMeasurementDTO convert(QuantityDTO source, QuantityDTO targetUnit);

    QuantityMeasurementDTO add(QuantityDTO q1, QuantityDTO q2);

    QuantityMeasurementDTO subtract(QuantityDTO q1, QuantityDTO q2);

    QuantityMeasurementDTO divide(QuantityDTO q1, QuantityDTO q2);

    List<QuantityMeasurementDTO> getHistoryByOperation(String operation);

    List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType);

    long getOperationCount(String operation);

    List<QuantityMeasurementDTO> getErrorHistory();
}
