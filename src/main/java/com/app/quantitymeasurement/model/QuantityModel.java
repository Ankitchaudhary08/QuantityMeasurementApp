package com.app.quantitymeasurement.model;

import com.app.quantitymeasurement.unit.IMeasurable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QuantityModel wraps a value and its unit, delegating operations to Quantity.
 */
@Data
@NoArgsConstructor
public class QuantityModel<U extends IMeasurable> {

    private double value;
    private U unit;
    private com.app.quantitymeasurement.unit.Quantity<U> quantity;

    public QuantityModel(double value, U unit) {
        this.value = value;
        this.unit = unit;
        this.quantity = new com.app.quantitymeasurement.unit.Quantity<>(value, unit);
    }

    public com.app.quantitymeasurement.unit.Quantity<U> getQuantity() {
        return quantity;
    }
}
