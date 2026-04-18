package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.model.*;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import com.app.quantitymeasurement.unit.IMeasurable;
import com.app.quantitymeasurement.unit.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * QuantityMeasurementServiceImpl — Spring Service layer implementation (UC17).
 * Uses Spring Data JPA repository for persistence; returns QuantityMeasurementDTO.
 */
@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementServiceImpl.class);

    @Autowired
    private QuantityMeasurementRepository repository;

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    /**
     * Convert "LengthUnit" -> "LENGTH", "WeightUnit" -> "WEIGHT", etc.
     * for compatibility with IMeasurable.getUnitInstance() which uses uppercase category names.
     */
    private String measurementTypeToCategory(String measurementType) {
        if (measurementType == null) return "UNKNOWN";
        switch (measurementType) {
            case "LengthUnit": return "LENGTH";
            case "WeightUnit": return "WEIGHT";
            case "VolumeUnit": return "VOLUME";
            case "TemperatureUnit": return "TEMPERATURE";
            default:
                // Try stripping "Unit" suffix and uppercasing
                String stripped = measurementType.replace("Unit", "").toUpperCase();
                return stripped.isEmpty() ? measurementType.toUpperCase() : stripped;
        }
    }

    private IMeasurable resolveUnit(QuantityDTO dto) {
        String category = measurementTypeToCategory(dto.getMeasurementType());
        return IMeasurable.getUnitInstance(category, dto.getUnit());
    }

    @SuppressWarnings("unchecked")
    private <U extends IMeasurable> com.app.quantitymeasurement.unit.Quantity<U> toQuantity(QuantityDTO dto) {
        U unit = (U) resolveUnit(dto);
        return new Quantity<>(dto.getValue(), unit);
    }

    private QuantityMeasurementEntity buildEntity(QuantityDTO q1, QuantityDTO q2, String operation) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        entity.setThisValue(q1.getValue());
        entity.setThisUnit(q1.getUnit());
        entity.setThisMeasurementType(q1.getMeasurementType());
        entity.setThatValue(q2.getValue());
        entity.setThatUnit(q2.getUnit());
        entity.setThatMeasurementType(q2.getMeasurementType());
        entity.setOperation(operation);
        entity.setError(false);
        return entity;
    }

    private QuantityMeasurementEntity buildErrorEntity(QuantityDTO q1, QuantityDTO q2, String operation, String errorMsg) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        if (q1 != null) {
            entity.setThisValue(q1.getValue());
            entity.setThisUnit(q1.getUnit() != null ? q1.getUnit() : "UNKNOWN");
            entity.setThisMeasurementType(q1.getMeasurementType() != null ? q1.getMeasurementType() : "UNKNOWN");
        } else {
            entity.setThisUnit("UNKNOWN");
            entity.setThisMeasurementType("UNKNOWN");
        }
        if (q2 != null) {
            entity.setThatValue(q2.getValue());
            entity.setThatUnit(q2.getUnit() != null ? q2.getUnit() : "UNKNOWN");
            entity.setThatMeasurementType(q2.getMeasurementType() != null ? q2.getMeasurementType() : "UNKNOWN");
        } else {
            entity.setThatUnit("UNKNOWN");
            entity.setThatMeasurementType("UNKNOWN");
        }
        entity.setOperation(operation != null ? operation : "UNKNOWN");
        entity.setError(true);
        entity.setErrorMessage(errorMsg);
        return entity;
    }

    // ----------------------------------------------------------------
    // Service Operations
    // ----------------------------------------------------------------

    @Override
    public QuantityMeasurementDTO compare(QuantityDTO q1, QuantityDTO q2) {
        try {
            if (!q1.getMeasurementType().equals(q2.getMeasurementType()))
                throw new QuantityMeasurementException("compare Error: Cannot compare different measurement categories: "
                        + q1.getMeasurementType() + " and " + q2.getMeasurementType());

            Quantity<IMeasurable> qty1 = toQuantity(q1);
            Quantity<IMeasurable> qty2 = toQuantity(q2);
            boolean result = qty1.equals(qty2);

            QuantityMeasurementEntity entity = buildEntity(q1, q2, "compare");
            entity.setResultString(String.valueOf(result));
            QuantityMeasurementEntity saved = repository.save(entity);
            logger.info("compare: {} == {} -> {}", q1, q2, result);
            return QuantityMeasurementDTO.fromEntity(saved);
        } catch (QuantityMeasurementException e) {
            logger.error("Compare failed: {}", e.getMessage());
            QuantityMeasurementEntity err = buildErrorEntity(q1, q2, "compare", e.getMessage());
            repository.save(err);
            throw e;
        } catch (Exception e) {
            logger.error("Compare unexpected error: {}", e.getMessage());
            QuantityMeasurementEntity err = buildErrorEntity(q1, q2, "compare", e.getMessage());
            repository.save(err);
            throw new QuantityMeasurementException("compare Error: " + e.getMessage(), e);
        }
    }

    @Override
    public QuantityMeasurementDTO convert(QuantityDTO source, QuantityDTO targetUnitDto) {
        try {
            Quantity<IMeasurable> qty = toQuantity(source);
            IMeasurable targetUnit = resolveUnit(targetUnitDto);
            Quantity<IMeasurable> converted = qty.convertTo(targetUnit);

            QuantityMeasurementEntity entity = buildEntity(source, targetUnitDto, "convert");
            entity.setResultValue(converted.getValue());
            entity.setResultUnit(converted.getUnit().getUnitName());
            entity.setResultMeasurementType(converted.getUnit().getMeasurementType());
            QuantityMeasurementEntity saved = repository.save(entity);
            logger.info("convert: {} -> {} = {}", source, targetUnitDto.getUnit(), converted.getValue());
            return QuantityMeasurementDTO.fromEntity(saved);
        } catch (QuantityMeasurementException e) {
            logger.error("Convert failed: {}", e.getMessage());
            QuantityMeasurementEntity err = buildErrorEntity(source, targetUnitDto, "convert", e.getMessage());
            repository.save(err);
            throw e;
        } catch (Exception e) {
            logger.error("Convert unexpected error: {}", e.getMessage());
            QuantityMeasurementEntity err = buildErrorEntity(source, targetUnitDto, "convert", e.getMessage());
            repository.save(err);
            throw new QuantityMeasurementException("convert Error: " + e.getMessage(), e);
        }
    }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO q1, QuantityDTO q2) {
        try {
            if (!q1.getMeasurementType().equals(q2.getMeasurementType()))
                throw new QuantityMeasurementException("add Error: Cannot perform arithmetic between different measurement categories: "
                        + q1.getMeasurementType() + " and " + q2.getMeasurementType());

            Quantity<IMeasurable> qty1 = toQuantity(q1);
            Quantity<IMeasurable> qty2 = toQuantity(q2);
            Quantity<IMeasurable> result = qty1.add(qty2);

            QuantityMeasurementEntity entity = buildEntity(q1, q2, "add");
            entity.setResultValue(result.getValue());
            entity.setResultUnit(result.getUnit().getUnitName());
            entity.setResultMeasurementType(result.getUnit().getMeasurementType());
            QuantityMeasurementEntity saved = repository.save(entity);
            logger.info("add: {} + {} = {}", q1, q2, result.getValue());
            return QuantityMeasurementDTO.fromEntity(saved);
        } catch (QuantityMeasurementException e) {
            logger.error("Add failed: {}", e.getMessage());
            QuantityMeasurementEntity err = buildErrorEntity(q1, q2, "add", e.getMessage());
            repository.save(err);
            throw e;
        } catch (Exception e) {
            logger.error("Add unexpected error: {}", e.getMessage());
            QuantityMeasurementEntity err = buildErrorEntity(q1, q2, "add", e.getMessage());
            repository.save(err);
            throw new QuantityMeasurementException("add Error: " + e.getMessage(), e);
        }
    }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO q1, QuantityDTO q2) {
        try {
            if (!q1.getMeasurementType().equals(q2.getMeasurementType()))
                throw new QuantityMeasurementException("subtract Error: Cannot perform arithmetic between different measurement categories: "
                        + q1.getMeasurementType() + " and " + q2.getMeasurementType());

            Quantity<IMeasurable> qty1 = toQuantity(q1);
            Quantity<IMeasurable> qty2 = toQuantity(q2);
            Quantity<IMeasurable> result = qty1.subtract(qty2);

            QuantityMeasurementEntity entity = buildEntity(q1, q2, "subtract");
            entity.setResultValue(result.getValue());
            entity.setResultUnit(result.getUnit().getUnitName());
            entity.setResultMeasurementType(result.getUnit().getMeasurementType());
            QuantityMeasurementEntity saved = repository.save(entity);
            logger.info("subtract: {} - {} = {}", q1, q2, result.getValue());
            return QuantityMeasurementDTO.fromEntity(saved);
        } catch (QuantityMeasurementException e) {
            logger.error("Subtract failed: {}", e.getMessage());
            QuantityMeasurementEntity err = buildErrorEntity(q1, q2, "subtract", e.getMessage());
            repository.save(err);
            throw e;
        } catch (Exception e) {
            logger.error("Subtract unexpected error: {}", e.getMessage());
            QuantityMeasurementEntity err = buildErrorEntity(q1, q2, "subtract", e.getMessage());
            repository.save(err);
            throw new QuantityMeasurementException("subtract Error: " + e.getMessage(), e);
        }
    }

    @Override
    public QuantityMeasurementDTO divide(QuantityDTO q1, QuantityDTO q2) {
        try {
            if (!q1.getMeasurementType().equals(q2.getMeasurementType()))
                throw new QuantityMeasurementException("divide Error: Cannot perform arithmetic between different measurement categories: "
                        + q1.getMeasurementType() + " and " + q2.getMeasurementType());

            Quantity<IMeasurable> qty1 = toQuantity(q1);
            Quantity<IMeasurable> qty2 = toQuantity(q2);
            double result = qty1.divide(qty2);

            QuantityMeasurementEntity entity = buildEntity(q1, q2, "divide");
            entity.setResultValue(result);
            QuantityMeasurementEntity saved = repository.save(entity);
            logger.info("divide: {} / {} = {}", q1, q2, result);
            return QuantityMeasurementDTO.fromEntity(saved);
        } catch (QuantityMeasurementException e) {
            logger.error("Divide failed: {}", e.getMessage());
            QuantityMeasurementEntity err = buildErrorEntity(q1, q2, "divide", e.getMessage());
            repository.save(err);
            throw e;
        } catch (Exception e) {
            logger.error("Divide unexpected error: {}", e.getMessage());
            QuantityMeasurementEntity err = buildErrorEntity(q1, q2, "divide", e.getMessage());
            repository.save(err);
            throw new QuantityMeasurementException("Divide by zero", e);
        }
    }

    // ----------------------------------------------------------------
    // History / Query Operations
    // ----------------------------------------------------------------

    @Override
    public List<QuantityMeasurementDTO> getHistoryByOperation(String operation) {
        List<QuantityMeasurementEntity> entities = repository.findByOperation(operation.toLowerCase());
        return QuantityMeasurementDTO.fromEntityList(entities);
    }

    @Override
    public List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType) {
        List<QuantityMeasurementEntity> entities = repository.findByThisMeasurementType(measurementType);
        return QuantityMeasurementDTO.fromEntityList(entities);
    }

    @Override
    public long getOperationCount(String operation) {
        return repository.countByOperationAndErrorFalse(operation.toLowerCase());
    }

    @Override
    public List<QuantityMeasurementDTO> getErrorHistory() {
        List<QuantityMeasurementEntity> entities = repository.findByErrorTrue();
        return QuantityMeasurementDTO.fromEntityList(entities);
    }
}
