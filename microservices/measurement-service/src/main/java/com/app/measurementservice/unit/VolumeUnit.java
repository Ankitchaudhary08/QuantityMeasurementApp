package com.app.measurementservice.unit;

public enum VolumeUnit implements IMeasurable {

    LITRE(1.0), MILLILITRE(0.001), GALLON(3.78541);

    private final double conversionFactorToLitre;

    VolumeUnit(double f) { this.conversionFactorToLitre = f; }

    @Override public double getConversionFactor()            { return conversionFactorToLitre; }
    @Override public double convertToBaseUnit(double v)      { return v * conversionFactorToLitre; }
    @Override public double convertFromBaseUnit(double base) { return base / conversionFactorToLitre; }
    @Override public String getUnitName()                    { return name(); }
    @Override public String getMeasurementType()             { return "VOLUME"; }
}
