package com.app.measurementservice.repository;

import com.app.measurementservice.model.MeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * MeasurementRepository — Spring Data JPA repository for measurement records.
 * History queries are served by the history-service (over REST).
 */
@Repository
public interface MeasurementRepository extends JpaRepository<MeasurementEntity, Long> {
}
