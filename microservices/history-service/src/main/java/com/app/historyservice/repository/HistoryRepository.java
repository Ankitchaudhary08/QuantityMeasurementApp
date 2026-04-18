package com.app.historyservice.repository;

import com.app.historyservice.model.HistoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryRecord, Long> {
    List<HistoryRecord> findByOperation(String operation);
    List<HistoryRecord> findByThisMeasurementType(String measurementType);
    List<HistoryRecord> findByPerformedBy(String email);
    List<HistoryRecord> findByErrorTrue();
    long countByOperationAndErrorFalse(String operation);
}
