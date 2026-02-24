package UtilityClasses;

public enum LengthUnit {
    FEET(1.0),
    INCH(1.0 / 12.0);

    private final double baseUnitConversionFactor;

    LengthUnit(double baseUnitConversionFactor) {
        this.baseUnitConversionFactor = baseUnitConversionFactor;
    }

    public double convertToBaseUnit(double value) {
        return value * baseUnitConversionFactor;
    }
}
