package UtilityClasses;

public enum WeightUnit implements IMeasurable {

    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592); // 1 lb = 0.453592 kg

    private final double conversionFactorToKg;

    WeightUnit(double conversionFactorToKg) {
        this.conversionFactorToKg = conversionFactorToKg;
    }


    public double convertToBaseUnit(double value) {
        return value * conversionFactorToKg;
    }


    public double convertFromBaseUnit(double baseValue) {
        return baseValue / conversionFactorToKg;
    }

    @Override
    public String getUnitName() {
        return null;
    }

    public double getConversionFactor() {
        return conversionFactorToKg;
    }
}