package com.app.quantitymeasurement.unit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class QuantityTest {

    private static final Logger log = LoggerFactory.getLogger(QuantityTest.class);
    private static final double EPS = 1e-6;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void beforeEach() {
        log.info("▶  RUNNING : {}", testName.getMethodName());
    }

    @After
    public void afterEach() {
        log.info("✔  PASSED  : {}", testName.getMethodName());
    }

    // ---- LENGTH ----

    @Test
    public void testLengthEquality() {
        Quantity<LengthUnit> f = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> i = new Quantity<>(12.0, LengthUnit.INCH);
        assertEquals(f, i);
    }

    @Test
    public void testLengthConversion() {
        Quantity<LengthUnit> f = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> result = f.convertTo(LengthUnit.INCH);
        assertEquals(12.0, result.getValue(), EPS);
    }

    @Test
    public void testLengthAddition() {
        Quantity<LengthUnit> f = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> i = new Quantity<>(12.0, LengthUnit.INCH);
        Quantity<LengthUnit> result = f.add(i, LengthUnit.FEET);
        assertEquals(2.0, result.getValue(), EPS);
    }

    // ---- WEIGHT ----

    @Test
    public void testWeightEquality() {
        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> g = new Quantity<>(1000.0, WeightUnit.GRAM);
        assertEquals(kg, g);
    }

    @Test
    public void testWeightConversion() {
        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> result = kg.convertTo(WeightUnit.GRAM);
        assertEquals(1000.0, result.getValue(), EPS);
    }

    @Test
    public void testWeightAddition() {
        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> g = new Quantity<>(1000.0, WeightUnit.GRAM);
        Quantity<WeightUnit> result = kg.add(g, WeightUnit.KILOGRAM);
        assertEquals(2.0, result.getValue(), EPS);
    }

    // ---- VOLUME ----

    @Test
    public void testVolumeEquality_LitreToMillilitre() {
        Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> ml = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        assertEquals(litre, ml);
    }

    @Test
    public void testVolumeConversion_LitreToMillilitre() {
        Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> result = litre.convertTo(VolumeUnit.MILLILITRE);
        assertEquals(1000.0, result.getValue(), EPS);
    }

    @Test
    public void testVolumeAddition_LitrePlusMillilitre() {
        Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> ml = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> result = litre.add(ml);
        assertEquals(2.0, result.getValue(), EPS);
        assertEquals(VolumeUnit.LITRE, result.getUnit());
    }

    // ---- CROSS-CATEGORY ----

    @Test
    public void testCrossCategoryNotEqual() {
        Quantity<LengthUnit> length = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<WeightUnit> weight = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        assertNotEquals(length, weight);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUnitThrows() {
        new Quantity<>(1.0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInfiniteValueThrows() {
        new Quantity<>(Double.NaN, LengthUnit.FEET);
    }

    // ---- SUBTRACTION ----

    @Test
    public void testSubtraction_SameUnit_Length() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(5.0, LengthUnit.FEET);
        Quantity<LengthUnit> result = q1.subtract(q2);
        assertEquals(5.0, result.getValue(), EPS);
    }

    @Test
    public void testSubtraction_CrossUnit() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(6.0, LengthUnit.INCH);
        Quantity<LengthUnit> result = q1.subtract(q2);
        assertEquals(9.5, result.getValue(), EPS);
    }

    // ---- TEMPERATURE ----

    @Test
    public void testTemperatureEquality_CelsiusVsFahrenheit() {
        Quantity<TemperatureUnit> celsius = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> fahrenheit = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);
        assertEquals(celsius, fahrenheit);
    }

    @Test
    public void testTemperatureConversion_CelsiusToFahrenheit() {
        Quantity<TemperatureUnit> celsius = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> result = celsius.convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(212.0, result.getValue(), EPS);
    }
}
