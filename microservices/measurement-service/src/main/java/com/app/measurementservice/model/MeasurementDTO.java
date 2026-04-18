package com.app.measurementservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MeasurementDTO — API response/request transfer object for measurement operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementDTO {

    private Long   id;
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
    private String resultMeasurementType;
    private String errorMessage;
    private boolean error;
    private String performedBy;
    private LocalDateTime createdAt;

    public static MeasurementDTO fromEntity(MeasurementEntity e) {
        MeasurementDTO d = new MeasurementDTO();
        d.setId(e.getId());
        d.setThisValue(e.getThisValue());
        d.setThisUnit(e.getThisUnit());
        d.setThisMeasurementType(e.getThisMeasurementType());
        d.setThatValue(e.getThatValue());
        d.setThatUnit(e.getThatUnit());
        d.setThatMeasurementType(e.getThatMeasurementType());
        d.setOperation(e.getOperation());
        d.setResultString(e.getResultString());
        d.setResultValue(e.getResultValue());
        d.setResultUnit(e.getResultUnit());
        d.setResultMeasurementType(e.getResultMeasurementType());
        d.setErrorMessage(e.getErrorMessage());
        d.setError(e.isError());
        d.setPerformedBy(e.getPerformedBy());
        d.setCreatedAt(e.getCreatedAt());
        return d;
    }

    public static List<MeasurementDTO> fromEntityList(List<MeasurementEntity> entities) {
        return entities.stream().map(MeasurementDTO::fromEntity).collect(Collectors.toList());
    }
}
