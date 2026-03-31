package com.app.quantitymeasurement.unit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QuantityTest — Pure unit tests for the Quantity business domain logic.
 * Migrated from JUnit 4 to JUnit 5 for UC17 Spring Boot compatibility.
 */
class QuantityTest {

    private static final Logger log = LoggerFactory.getLogger(QuantityTest.class);
    private static final double EPS = 1e-6;

    @BeforeEach
    void beforeEach(TestInfo info) {
        log.info("▶  RUNNING : {}", info.getDisplayName());
    }

    @AfterEach
    void afterEach(TestInfo info) {
        log.info("✔  PASSED  : {}", info.getDisplayName());
    }

    // ---- LENGTH ----

    @Test
    void testLengthEquality() {
        Quantity<LengthUnit> f = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> i = new Quantity<>(12.0, LengthUnit.INCH);
        assertEquals(f, i);
    }

    @Test
    void testLengthConversion() {
        Quantity<LengthUnit> f = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> result = f.convertTo(LengthUnit.INCH);
        assertEquals(12.0, result.getValue(), EPS);
    }

    @Test
    void testLengthAddition() {
        Quantity<LengthUnit> f = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> i = new Quantity<>(12.0, LengthUnit.INCH);
        Quantity<LengthUnit> result = f.add(i, LengthUnit.FEET);
        assertEquals(2.0, result.getValue(), EPS);
    }

    // ---- WEIGHT ----

    @Test
    void testWeightEquality() {
        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> g = new Quantity<>(1000.0, WeightUnit.GRAM);
        assertEquals(kg, g);
    }

    @Test
    void testWeightConversion() {
        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> result = kg.convertTo(WeightUnit.GRAM);
        assertEquals(1000.0, result.getValue(), EPS);
    }

    @Test
    void testWeightAddition() {
        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> g = new Quantity<>(1000.0, WeightUnit.GRAM);
        Quantity<WeightUnit> result = kg.add(g, WeightUnit.KILOGRAM);
        assertEquals(2.0, result.getValue(), EPS);
    }

    // ---- VOLUME ----

    @Test
    void testVolumeEquality_LitreToMillilitre() {
        Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> ml = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        assertEquals(litre, ml);
    }

    @Test
    void testVolumeConversion_LitreToMillilitre() {
        Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> result = litre.convertTo(VolumeUnit.MILLILITRE);
        assertEquals(1000.0, result.getValue(), EPS);
    }

    @Test
    void testVolumeAddition_LitrePlusMillilitre() {
        Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> ml = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> result = litre.add(ml);
        assertEquals(2.0, result.getValue(), EPS);
        assertEquals(VolumeUnit.LITRE, result.getUnit());
    }

    // ---- CROSS-CATEGORY ----

    @Test
    void testCrossCategoryNotEqual() {
        Quantity<LengthUnit> length = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<WeightUnit> weight = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        assertNotEquals(length, weight);
    }

    @Test
    void testNullUnitThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity<>(1.0, null));
    }

    @Test
    void testInfiniteValueThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity<>(Double.NaN, LengthUnit.FEET));
    }

    // ---- SUBTRACTION ----

    @Test
    void testSubtraction_SameUnit_Length() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(5.0, LengthUnit.FEET);
        Quantity<LengthUnit> result = q1.subtract(q2);
        assertEquals(5.0, result.getValue(), EPS);
    }

    @Test
    void testSubtraction_CrossUnit() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(6.0, LengthUnit.INCH);
        Quantity<LengthUnit> result = q1.subtract(q2);
        assertEquals(9.5, result.getValue(), EPS);
    }

    // ---- TEMPERATURE ----

    @Test
    void testTemperatureEquality_CelsiusVsFahrenheit() {
        Quantity<TemperatureUnit> celsius = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> fahrenheit = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);
        assertEquals(celsius, fahrenheit);
    }

    @Test
    void testTemperatureConversion_CelsiusToFahrenheit() {
        Quantity<TemperatureUnit> celsius = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> result = celsius.convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(212.0, result.getValue(), EPS);
    }
}
