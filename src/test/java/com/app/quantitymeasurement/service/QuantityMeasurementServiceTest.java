package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for QuantityMeasurementServiceImpl using Mockito.
 * Repository is mocked — no Spring context loaded.
 */
@ExtendWith(MockitoExtension.class)
class QuantityMeasurementServiceTest {

    @Mock
    private QuantityMeasurementRepository mockRepo;

    @InjectMocks
    private QuantityMeasurementServiceImpl service;

    @BeforeEach
    void setUp() {
        // Return the entity passed to save() so fromEntity() can read fields
        Mockito.lenient().when(mockRepo.save(any(QuantityMeasurementEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ---- Compare ----

    @Test
    void testCompare_EqualQuantities_ReturnsTrueResult() {
        QuantityDTO q1 = new QuantityDTO(1.0, "FEET", "LengthUnit");
        QuantityDTO q2 = new QuantityDTO(12.0, "INCH", "LengthUnit");

        QuantityMeasurementDTO result = service.compare(q1, q2);

        assertThat(result).isNotNull();
        assertThat(result.isError()).isFalse();
        assertThat(result.getResultString()).isEqualTo("true");
        verify(mockRepo, times(1)).save(any());
    }

    @Test
    void testCompare_NotEqualQuantities_ReturnsFalseResult() {
        QuantityDTO q1 = new QuantityDTO(1.0, "FEET", "LengthUnit");
        QuantityDTO q2 = new QuantityDTO(10.0, "INCH", "LengthUnit");

        QuantityMeasurementDTO result = service.compare(q1, q2);

        assertThat(result.getResultString()).isEqualTo("false");
    }

    @Test
    void testCompare_DifferentTypes_ThrowsException() {
        QuantityDTO q1 = new QuantityDTO(1.0, "FEET", "LengthUnit");
        QuantityDTO q2 = new QuantityDTO(1.0, "KILOGRAM", "WeightUnit");

        assertThatThrownBy(() -> service.compare(q1, q2))
                .isInstanceOf(QuantityMeasurementException.class)
                .hasMessageContaining("Cannot compare");

        // Error should still be saved
        verify(mockRepo, times(1)).save(any());
    }

    // ---- Convert ----

    @Test
    void testConvert_FeetToInch_Returns12() {
        QuantityDTO source = new QuantityDTO(1.0, "FEET", "LengthUnit");
        QuantityDTO target = new QuantityDTO(0.0, "INCH", "LengthUnit");

        QuantityMeasurementDTO result = service.convert(source, target);

        assertThat(result.isError()).isFalse();
        assertThat(result.getResultValue()).isEqualTo(12.0);
    }

    @Test
    void testConvert_CelsiusToFahrenheit_Returns212() {
        QuantityDTO source = new QuantityDTO(100.0, "CELSIUS", "TemperatureUnit");
        QuantityDTO target = new QuantityDTO(0.0, "FAHRENHEIT", "TemperatureUnit");

        QuantityMeasurementDTO result = service.convert(source, target);

        assertThat(result.getResultValue()).isEqualTo(212.0, within(0.001));
    }

    @Test
    void testConvert_KilogramToGram_Returns1000() {
        QuantityDTO source = new QuantityDTO(1.0, "KILOGRAM", "WeightUnit");
        QuantityDTO target = new QuantityDTO(0.0, "GRAM", "WeightUnit");

        QuantityMeasurementDTO result = service.convert(source, target);

        assertThat(result.getResultValue()).isEqualTo(1000.0, within(0.001));
    }

    // ---- Add ----

    @Test
    void testAdd_SameType_ReturnsResult() {
        QuantityDTO q1 = new QuantityDTO(1.0, "FEET", "LengthUnit");
        QuantityDTO q2 = new QuantityDTO(12.0, "INCH", "LengthUnit");

        QuantityMeasurementDTO result = service.add(q1, q2);

        assertThat(result.isError()).isFalse();
        assertThat(result.getResultValue()).isEqualTo(2.0, within(0.001));
    }

    @Test
    void testAdd_DifferentTypes_ThrowsException() {
        QuantityDTO q1 = new QuantityDTO(1.0, "FEET", "LengthUnit");
        QuantityDTO q2 = new QuantityDTO(1.0, "KILOGRAM", "WeightUnit");

        assertThatThrownBy(() -> service.add(q1, q2))
                .isInstanceOf(QuantityMeasurementException.class)
                .hasMessageContaining("Cannot perform arithmetic");

        verify(mockRepo, times(1)).save(any());
    }

    // ---- Subtract ----

    @Test
    void testSubtract_SameType_ReturnsResult() {
        QuantityDTO q1 = new QuantityDTO(10.0, "FEET", "LengthUnit");
        QuantityDTO q2 = new QuantityDTO(5.0, "FEET", "LengthUnit");

        QuantityMeasurementDTO result = service.subtract(q1, q2);

        assertThat(result.isError()).isFalse();
        assertThat(result.getResultValue()).isEqualTo(5.0, within(0.001));
    }

    // ---- Divide ----

    @Test
    void testDivide_SameType_ReturnsDivisionResult() {
        QuantityDTO q1 = new QuantityDTO(10.0, "KILOGRAM", "WeightUnit");
        QuantityDTO q2 = new QuantityDTO(5.0, "KILOGRAM", "WeightUnit");

        QuantityMeasurementDTO result = service.divide(q1, q2);

        assertThat(result.isError()).isFalse();
        assertThat(result.getResultValue()).isEqualTo(2.0, within(1e-6));
    }

    @Test
    void testDivide_DifferentTypes_ThrowsException() {
        QuantityDTO q1 = new QuantityDTO(10.0, "KILOGRAM", "WeightUnit");
        QuantityDTO q2 = new QuantityDTO(5.0, "FEET", "LengthUnit");

        assertThatThrownBy(() -> service.divide(q1, q2))
                .isInstanceOf(QuantityMeasurementException.class);
    }

    // ---- Repository is always called ----

    @Test
    void testRepositoryIsAlwaysCalledOnOperation() {
        QuantityDTO q1 = new QuantityDTO(1.0, "FEET", "LengthUnit");
        QuantityDTO q2 = new QuantityDTO(12.0, "INCH", "LengthUnit");
        service.compare(q1, q2);
        verify(mockRepo, times(1)).save(any());
    }
}
