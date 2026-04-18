package com.app.measurementservice.unit;

import java.util.function.Function;

public enum TemperatureUnit implements IMeasurable {

    CELSIUS(c -> c, c -> c),
    FAHRENHEIT(f -> (f - 32) * 5.0 / 9.0, c -> c * 9.0 / 5.0 + 32),
    KELVIN(k -> k - 273.15, c -> c + 273.15);

    private final Function<Double, Double> toBaseFn;
    private final Function<Double, Double> fromBaseFn;
    private final SupportsArithmetic supportsArithmetic = () -> false;

    TemperatureUnit(Function<Double, Double> to, Function<Double, Double> from) {
        this.toBaseFn = to;
        this.fromBaseFn = from;
    }

    @Override public double getConversionFactor()            { return 1.0; }
    @Override public double convertToBaseUnit(double v)      { return toBaseFn.apply(v); }
    @Override public double convertFromBaseUnit(double base) { return fromBaseFn.apply(base); }
    @Override public String getUnitName()                    { return name(); }
    @Override public String getMeasurementType()             { return "TEMPERATURE"; }

    @Override
    public boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    @Override
    public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException(
                "Temperature does not support " + operation + " operation.");
    }
}
