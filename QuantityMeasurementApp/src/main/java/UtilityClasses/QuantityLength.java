package UtilityClasses;

public class QuantityLength {
    private final double value;
    private final LengthUnit unit;

    public QuantityLength(double value, LengthUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public LengthUnit getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        if (obj == null || !(obj instanceof QuantityLength)) return false;
        
        QuantityLength other = (QuantityLength) obj;
        
        double thisBaseValue = this.unit.convertToBaseUnit(this.value);
        double otherBaseValue = other.unit.convertToBaseUnit(other.value);
        
        // Handling floating point comparisons with rounding to avoid floating point precision issues
        return Double.compare(Math.round(thisBaseValue * 10000.0) / 10000.0,
                              Math.round(otherBaseValue * 10000.0) / 10000.0) == 0;
    }

    @Override
    public int hashCode() {
        double thisBaseValue = this.unit.convertToBaseUnit(this.value);
        return Double.hashCode(Math.round(thisBaseValue * 10000.0) / 10000.0);
    }
    
    @Override
    public String toString() {
        String unitName = unit == LengthUnit.INCH ? "inch" : "feet"; // basic lowercase formatting
        return "Quantity(" + value + ", \"" + unitName + "\")";
    }
}
