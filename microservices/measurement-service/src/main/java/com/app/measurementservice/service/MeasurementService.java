package com.app.measurementservice.service;

import com.app.measurementservice.exception.MeasurementException;
import com.app.measurementservice.model.*;
import com.app.measurementservice.repository.MeasurementRepository;
import com.app.measurementservice.unit.IMeasurable;
import com.app.measurementservice.unit.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MeasurementService — Core business logic for quantity operations.
 * Each operation persists a record and returns a MeasurementDTO.
 */
@Service
public class MeasurementService {

    private static final Logger logger = LoggerFactory.getLogger(MeasurementService.class);

    @Autowired
    private MeasurementRepository repository;

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private String typeToCategory(String measurementType) {
        if (measurementType == null) return "UNKNOWN";
        switch (measurementType) {
            case "LengthUnit":      return "LENGTH";
            case "WeightUnit":      return "WEIGHT";
            case "VolumeUnit":      return "VOLUME";
            case "TemperatureUnit": return "TEMPERATURE";
            default:
                String stripped = measurementType.replace("Unit", "").toUpperCase();
                return stripped.isEmpty() ? measurementType.toUpperCase() : stripped;
        }
    }

    @SuppressWarnings("unchecked")
    private <U extends IMeasurable> Quantity<U> toQuantity(QuantityDTO dto) {
        String category = typeToCategory(dto.getMeasurementType());
        U unit = (U) IMeasurable.getUnitInstance(category, dto.getUnit());
        return new Quantity<>(dto.getValue(), unit);
    }

    private IMeasurable resolveUnit(QuantityDTO dto) {
        String category = typeToCategory(dto.getMeasurementType());
        return IMeasurable.getUnitInstance(category, dto.getUnit());
    }

    private MeasurementEntity buildEntity(QuantityDTO q1, QuantityDTO q2, String op, String user) {
        MeasurementEntity e = new MeasurementEntity();
        e.setThisValue(q1.getValue());    e.setThisUnit(q1.getUnit());    e.setThisMeasurementType(q1.getMeasurementType());
        e.setThatValue(q2.getValue());    e.setThatUnit(q2.getUnit());    e.setThatMeasurementType(q2.getMeasurementType());
        e.setOperation(op);               e.setError(false);              e.setPerformedBy(user);
        return e;
    }

    private MeasurementEntity buildErrorEntity(QuantityDTO q1, QuantityDTO q2, String op, String msg, String user) {
        MeasurementEntity e = new MeasurementEntity();
        e.setThisValue(q1 != null ? q1.getValue() : 0);
        e.setThisUnit(q1 != null && q1.getUnit() != null ? q1.getUnit() : "UNKNOWN");
        e.setThisMeasurementType(q1 != null && q1.getMeasurementType() != null ? q1.getMeasurementType() : "UNKNOWN");
        e.setThatValue(q2 != null ? q2.getValue() : 0);
        e.setThatUnit(q2 != null && q2.getUnit() != null ? q2.getUnit() : "UNKNOWN");
        e.setThatMeasurementType(q2 != null && q2.getMeasurementType() != null ? q2.getMeasurementType() : "UNKNOWN");
        e.setOperation(op != null ? op : "UNKNOWN");
        e.setError(true);
        e.setErrorMessage(msg);
        e.setPerformedBy(user);
        return e;
    }

    // ----------------------------------------------------------------
    // Operations
    // ----------------------------------------------------------------

    public MeasurementDTO compare(QuantityDTO q1, QuantityDTO q2, String performedBy) {
        try {
            if (!q1.getMeasurementType().equals(q2.getMeasurementType()))
                throw new MeasurementException("Cannot compare different measurement types: "
                        + q1.getMeasurementType() + " vs " + q2.getMeasurementType());

            Quantity<IMeasurable> qty1 = toQuantity(q1);
            Quantity<IMeasurable> qty2 = toQuantity(q2);
            boolean result = qty1.equals(qty2);

            MeasurementEntity entity = buildEntity(q1, q2, "compare", performedBy);
            entity.setResultString(String.valueOf(result));
            logger.info("compare: {} == {} -> {}", q1, q2, result);
            return MeasurementDTO.fromEntity(repository.save(entity));
        } catch (MeasurementException e) {
            repository.save(buildErrorEntity(q1, q2, "compare", e.getMessage(), performedBy));
            throw e;
        } catch (Exception e) {
            repository.save(buildErrorEntity(q1, q2, "compare", e.getMessage(), performedBy));
            throw new MeasurementException("compare Error: " + e.getMessage(), e);
        }
    }

    public MeasurementDTO convert(QuantityDTO source, QuantityDTO targetUnitDto, String performedBy) {
        try {
            Quantity<IMeasurable> qty = toQuantity(source);
            IMeasurable targetUnit = resolveUnit(targetUnitDto);
            Quantity<IMeasurable> converted = qty.convertTo(targetUnit);

            MeasurementEntity entity = buildEntity(source, targetUnitDto, "convert", performedBy);
            entity.setResultValue(converted.getValue());
            entity.setResultUnit(converted.getUnit().getUnitName());
            entity.setResultMeasurementType(converted.getUnit().getMeasurementType());
            logger.info("convert: {} -> {} = {}", source, targetUnitDto.getUnit(), converted.getValue());
            return MeasurementDTO.fromEntity(repository.save(entity));
        } catch (MeasurementException e) {
            repository.save(buildErrorEntity(source, targetUnitDto, "convert", e.getMessage(), performedBy));
            throw e;
        } catch (Exception e) {
            repository.save(buildErrorEntity(source, targetUnitDto, "convert", e.getMessage(), performedBy));
            throw new MeasurementException("convert Error: " + e.getMessage(), e);
        }
    }

    public MeasurementDTO add(QuantityDTO q1, QuantityDTO q2, String performedBy) {
        try {
            if (!q1.getMeasurementType().equals(q2.getMeasurementType()))
                throw new MeasurementException("Cannot add different measurement types: "
                        + q1.getMeasurementType() + " vs " + q2.getMeasurementType());

            Quantity<IMeasurable> qty1 = toQuantity(q1);
            Quantity<IMeasurable> qty2 = toQuantity(q2);
            Quantity<IMeasurable> result = qty1.add(qty2);

            MeasurementEntity entity = buildEntity(q1, q2, "add", performedBy);
            entity.setResultValue(result.getValue());
            entity.setResultUnit(result.getUnit().getUnitName());
            entity.setResultMeasurementType(result.getUnit().getMeasurementType());
            logger.info("add: {} + {} = {}", q1, q2, result.getValue());
            return MeasurementDTO.fromEntity(repository.save(entity));
        } catch (MeasurementException e) {
            repository.save(buildErrorEntity(q1, q2, "add", e.getMessage(), performedBy));
            throw e;
        } catch (Exception e) {
            repository.save(buildErrorEntity(q1, q2, "add", e.getMessage(), performedBy));
            throw new MeasurementException("add Error: " + e.getMessage(), e);
        }
    }

    public MeasurementDTO subtract(QuantityDTO q1, QuantityDTO q2, String performedBy) {
        try {
            if (!q1.getMeasurementType().equals(q2.getMeasurementType()))
                throw new MeasurementException("Cannot subtract different measurement types: "
                        + q1.getMeasurementType() + " vs " + q2.getMeasurementType());

            Quantity<IMeasurable> qty1 = toQuantity(q1);
            Quantity<IMeasurable> qty2 = toQuantity(q2);
            Quantity<IMeasurable> result = qty1.subtract(qty2);

            MeasurementEntity entity = buildEntity(q1, q2, "subtract", performedBy);
            entity.setResultValue(result.getValue());
            entity.setResultUnit(result.getUnit().getUnitName());
            entity.setResultMeasurementType(result.getUnit().getMeasurementType());
            logger.info("subtract: {} - {} = {}", q1, q2, result.getValue());
            return MeasurementDTO.fromEntity(repository.save(entity));
        } catch (MeasurementException e) {
            repository.save(buildErrorEntity(q1, q2, "subtract", e.getMessage(), performedBy));
            throw e;
        } catch (Exception e) {
            repository.save(buildErrorEntity(q1, q2, "subtract", e.getMessage(), performedBy));
            throw new MeasurementException("subtract Error: " + e.getMessage(), e);
        }
    }

    public MeasurementDTO divide(QuantityDTO q1, QuantityDTO q2, String performedBy) {
        try {
            if (!q1.getMeasurementType().equals(q2.getMeasurementType()))
                throw new MeasurementException("Cannot divide different measurement types: "
                        + q1.getMeasurementType() + " vs " + q2.getMeasurementType());

            Quantity<IMeasurable> qty1 = toQuantity(q1);
            Quantity<IMeasurable> qty2 = toQuantity(q2);
            double result = qty1.divide(qty2);

            MeasurementEntity entity = buildEntity(q1, q2, "divide", performedBy);
            entity.setResultValue(result);
            logger.info("divide: {} / {} = {}", q1, q2, result);
            return MeasurementDTO.fromEntity(repository.save(entity));
        } catch (MeasurementException e) {
            repository.save(buildErrorEntity(q1, q2, "divide", e.getMessage(), performedBy));
            throw e;
        } catch (Exception e) {
            repository.save(buildErrorEntity(q1, q2, "divide", e.getMessage(), performedBy));
            throw new MeasurementException("Divide by zero or invalid input", e);
        }
    }
}
