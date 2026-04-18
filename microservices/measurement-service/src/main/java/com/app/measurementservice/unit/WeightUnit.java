package com.app.measurementservice.unit;

public enum WeightUnit implements IMeasurable {

    KILOGRAM(1.0), GRAM(0.001), POUND(0.453592);

    private final double conversionFactorToKilogram;

    WeightUnit(double f) { this.conversionFactorToKilogram = f; }

    @Override public double getConversionFactor()            { return conversionFactorToKilogram; }
    @Override public double convertToBaseUnit(double v)      { return v * conversionFactorToKilogram; }
    @Override public double convertFromBaseUnit(double base) { return base / conversionFactorToKilogram; }
    @Override public String getUnitName()                    { return name(); }
    @Override public String getMeasurementType()             { return "WEIGHT"; }
}
