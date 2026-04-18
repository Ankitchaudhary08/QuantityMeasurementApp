package com.app.measurementservice.unit;

public enum LengthUnit implements IMeasurable {

    FEET(1.0), INCH(1.0 / 12.0), YARDS(3.0), CENTIMETERS(1.0 / 30.48);

    private final double conversionFactorToFeet;

    LengthUnit(double f) { this.conversionFactorToFeet = f; }

    @Override public double getConversionFactor()            { return conversionFactorToFeet; }
    @Override public double convertToBaseUnit(double v)      { return v * conversionFactorToFeet; }
    @Override public double convertFromBaseUnit(double base) { return base / conversionFactorToFeet; }
    @Override public String getUnitName()                    { return name(); }
    @Override public String getMeasurementType()             { return "LENGTH"; }
}
