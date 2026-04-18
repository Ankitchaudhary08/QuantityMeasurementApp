package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for QuantityMeasurementEntity.
 * Provides CRUD + custom derived/JPQL query methods.
 */
@Repository
public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {

    /** Find all records for a given operation (e.g. "compare", "add"). */
    List<QuantityMeasurementEntity> findByOperation(String operation);

    /** Find all records for a given measurement type (e.g. "LengthUnit"). */
    List<QuantityMeasurementEntity> findByThisMeasurementType(String measurementType);

    /** Find all records created after a given date/time. */
    List<QuantityMeasurementEntity> findByCreatedAtAfter(LocalDateTime date);

    /** Custom JPQL: successful operations matching a specific operation type. */
    @Query("SELECT q FROM QuantityMeasurementEntity q WHERE q.operation = :operation AND q.error = false")
    List<QuantityMeasurementEntity> findSuccessfulByOperation(@Param("operation") String operation);

    /** Count successful records for a given operation. */
    long countByOperationAndErrorFalse(String operation);

    /** Find all records that have errors. */
    List<QuantityMeasurementEntity> findByErrorTrue();
}
