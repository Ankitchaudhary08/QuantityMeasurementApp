package UtilityClasses;

import junit.framework.TestCase;

public class QuantityLengthTest extends TestCase {

    public void testEquality_FeetToFeet_SameValue() {
        QuantityLength val1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength val2 = new QuantityLength(1.0, LengthUnit.FEET);
        assertTrue(val1.equals(val2));
    }

    public void testEquality_InchToInch_SameValue() {
        QuantityLength val1 = new QuantityLength(1.0, LengthUnit.INCH);
        QuantityLength val2 = new QuantityLength(1.0, LengthUnit.INCH);
        assertTrue(val1.equals(val2));
    }

    public void testEquality_FeetToInch_EquivalentValue() {
        QuantityLength val1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength val2 = new QuantityLength(12.0, LengthUnit.INCH);
        assertTrue(val1.equals(val2));
    }

    public void testEquality_InchToFeet_EquivalentValue() {
        QuantityLength val1 = new QuantityLength(12.0, LengthUnit.INCH);
        QuantityLength val2 = new QuantityLength(1.0, LengthUnit.FEET);
        assertTrue(val1.equals(val2));
    }

    public void testEquality_FeetToFeet_DifferentValue() {
        QuantityLength val1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength val2 = new QuantityLength(2.0, LengthUnit.FEET);
        assertFalse(val1.equals(val2));
    }

    public void testEquality_InchToInch_DifferentValue() {
        QuantityLength val1 = new QuantityLength(1.0, LengthUnit.INCH);
        QuantityLength val2 = new QuantityLength(2.0, LengthUnit.INCH);
        assertFalse(val1.equals(val2));
    }

    public void testEquality_NullUnit() {
        try {
            new QuantityLength(1.0, null);
            fail("Expected IllegalArgumentException for null unit.");
        } catch (IllegalArgumentException e) {
            assertEquals("Unit cannot be null", e.getMessage());
        }
    }

    public void testEquality_SameReference() {
        QuantityLength val1 = new QuantityLength(1.0, LengthUnit.FEET);
        assertTrue(val1.equals(val1));
    }

    public void testEquality_NullComparison() {
        QuantityLength val1 = new QuantityLength(1.0, LengthUnit.FEET);
        assertFalse(val1.equals(null));
    }

    public void testLegacyEquality_FeetClassToQuantity() {
        FeetEquality.Feet f1 = new FeetEquality.Feet(1.0);
        QuantityLength q = new QuantityLength(1.0, LengthUnit.FEET);
        assertTrue(f1.equals(q));
    }

    public void testLegacyEquality_InchesClassToFeetClass() {
        InchesEquality.Inches i1 = new InchesEquality.Inches(12.0);
        FeetEquality.Feet f1 = new FeetEquality.Feet(1.0);
        assertTrue(i1.equals(f1));
    }
}
