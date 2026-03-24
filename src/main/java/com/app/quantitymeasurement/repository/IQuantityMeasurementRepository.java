package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;

import java.util.List;

/**
 * IQuantityMeasurementRepository — Repository interface for UC16 JDBC
 * persistence.
 * All UC1–UC15 implementations remain backward compatible.
 */
public interface IQuantityMeasurementRepository {

    void save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> getAllMeasurements();

    List<QuantityMeasurementEntity> getMeasurementsByOperation(String operationType);

    List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType);

    int getTotalCount();

    void deleteAll();

    /** Returns connection pool statistics (default: no pool). */
    default String getPoolStatistics() {
        return "No pool statistics available";
    }

    /** Releases resources held by the repository (e.g. DB connections). */
    default void releaseResources() {
    }
}
