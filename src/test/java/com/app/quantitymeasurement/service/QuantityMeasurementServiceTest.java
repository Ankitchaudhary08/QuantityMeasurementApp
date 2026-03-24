package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for QuantityMeasurementServiceImpl using Mockito to mock the
 * repository.
 */
public class QuantityMeasurementServiceTest {

    private static final Logger log = LoggerFactory.getLogger(QuantityMeasurementServiceTest.class);

    @Rule
    public TestName testName = new TestName();

    private IQuantityMeasurementRepository mockRepo;
    private QuantityMeasurementServiceImpl service;

    @Before
    public void setUp() {
        log.info("▶  RUNNING : {}", testName.getMethodName());
        mockRepo = Mockito.mock(IQuantityMeasurementRepository.class);
        service = new QuantityMeasurementServiceImpl(mockRepo);
    }

    @After
    public void tearDown() {
        log.info("✔  PASSED  : {}", testName.getMethodName());
    }

    // ---- Compare ----

    @Test
    public void testCompare_SameType_Equal() {
        QuantityMeasurementEntity result = service.compare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH));

        assertFalse(result.hasError());
        assertTrue(result.isComparison());
        assertTrue(result.getComparisonResult());
        verify(mockRepo, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    public void testCompare_DifferentType_ReturnsError() {
        QuantityMeasurementEntity result = service.compare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM));

        assertTrue(result.hasError());
        assertNotNull(result.getErrorMessage());
        verify(mockRepo, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    // ---- Convert ----

    @Test
    public void testConvert_FeetToInch() {
        QuantityMeasurementEntity result = service.convert(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCH));

        assertFalse(result.hasError());
        assertNotNull(result.getResult());
        assertEquals(12.0, result.getResult().getValue(), 1e-6);
    }

    @Test
    public void testConvert_CelsiusToFahrenheit() {
        QuantityMeasurementEntity result = service.convert(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.FAHRENHEIT));

        assertFalse(result.hasError());
        assertEquals(212.0, result.getResult().getValue(), 1e-6);
    }

    // ---- Add ----

    @Test
    public void testAdd_SameCategory() {
        QuantityMeasurementEntity result = service.add(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM));

        assertFalse(result.hasError());
        assertNotNull(result.getResult());
    }

    @Test
    public void testAdd_DifferentCategory_ReturnsError() {
        QuantityMeasurementEntity result = service.add(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM));

        assertTrue(result.hasError());
    }

    // ---- Subtract ----

    @Test
    public void testSubtract_SameCategory() {
        QuantityMeasurementEntity result = service.subtract(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(5.0, QuantityDTO.LengthUnit.FEET));

        assertFalse(result.hasError());
    }

    // ---- Divide ----

    @Test
    public void testDivide_SameCategory() {
        QuantityMeasurementEntity result = service.divide(
                new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(5.0, QuantityDTO.WeightUnit.KILOGRAM));

        assertFalse(result.hasError());
        assertTrue(result.hasScalarResult());
        assertEquals(2.0, result.getScalarResult(), 1e-6);
    }

    @Test
    public void testDivide_DifferentCategory_ReturnsError() {
        QuantityMeasurementEntity result = service.divide(
                new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(5.0, QuantityDTO.LengthUnit.FEET));

        assertTrue(result.hasError());
    }

    // ---- Repository always called ----

    @Test
    public void testRepositoryIsAlwaysCalledOnError() {
        service.compare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM));
        verify(mockRepo, times(1)).save(any(QuantityMeasurementEntity.class));
    }
}
