package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

/**
 * Unit tests for QuantityMeasurementController using Mockito-mocked service.
 */
public class QuantityMeasurementControllerTest {

    private IQuantityMeasurementService mockService;
    private QuantityMeasurementController controller;

    @Before
    public void setUp() {
        mockService = Mockito.mock(IQuantityMeasurementService.class);
        controller = new QuantityMeasurementController(mockService);
    }

    @Test
    public void testPerformComparison_DelegatestoService() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH);

        QuantityMeasurementEntity mockResult = new QuantityMeasurementEntity(q1, q2, "COMPARE", true);
        when(mockService.compare(q1, q2)).thenReturn(mockResult);

        controller.performComparison(q1, q2);
        verify(mockService, times(1)).compare(q1, q2);
    }

    @Test
    public void testPerformConversion_DelegatestoService() {
        QuantityDTO source = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO target = new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCH);

        QuantityMeasurementEntity mockResult = new QuantityMeasurementEntity(source, target, "CONVERT");
        QuantityDTO resultDto = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH);
        mockResult.setResult(resultDto);
        when(mockService.convert(source, target)).thenReturn(mockResult);

        controller.performConversion(source, target);
        verify(mockService, times(1)).convert(source, target);
    }

    @Test
    public void testPerformAddition_DelegatestoService() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM);
        QuantityDTO q2 = new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM);

        QuantityMeasurementEntity mockResult = new QuantityMeasurementEntity(q1, q2, "ADD");
        when(mockService.add(q1, q2)).thenReturn(mockResult);

        controller.performAddition(q1, q2);
        verify(mockService, times(1)).add(q1, q2);
    }

    @Test
    public void testPerformSubtraction_DelegatestoService() {
        QuantityDTO q1 = new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(5.0, QuantityDTO.LengthUnit.FEET);

        QuantityMeasurementEntity mockResult = new QuantityMeasurementEntity(q1, q2, "SUBTRACT");
        when(mockService.subtract(q1, q2)).thenReturn(mockResult);

        controller.performSubtraction(q1, q2);
        verify(mockService, times(1)).subtract(q1, q2);
    }

    @Test
    public void testPerformDivision_DelegatestoService() {
        QuantityDTO q1 = new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM);
        QuantityDTO q2 = new QuantityDTO(5.0, QuantityDTO.WeightUnit.KILOGRAM);

        QuantityMeasurementEntity mockResult = new QuantityMeasurementEntity(q1, q2, "DIVIDE");
        mockResult.setScalarResult(2.0);
        when(mockService.divide(q1, q2)).thenReturn(mockResult);

        controller.performDivision(q1, q2);
        verify(mockService, times(1)).divide(q1, q2);
    }

    @Test
    public void testPerformComparison_ErrorResult_Logged() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM);

        QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity("Cannot compare different categories");
        when(mockService.compare(q1, q2)).thenReturn(errorEntity);

        // Should not throw — controller logs the error
        controller.performComparison(q1, q2);
        verify(mockService, times(1)).compare(q1, q2);
    }
}
