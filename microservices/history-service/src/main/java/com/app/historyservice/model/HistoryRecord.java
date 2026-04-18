package com.app.historyservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "history_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double thisValue;
    private String thisUnit;
    private String thisMeasurementType;

    private double thatValue;
    private String thatUnit;
    private String thatMeasurementType;

    private String operation;
    private String resultString;
    private double resultValue;
    private String resultUnit;

    private String errorMessage;
    private boolean error;

    private String performedBy;
    private LocalDateTime createdAt;
}
