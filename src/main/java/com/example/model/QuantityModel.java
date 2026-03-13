package com.example.model;

import com.example.domain.Measurable;
import com.example.domain.MeasurementType;
import java.io.Serializable;
import java.util.Objects;

public class QuantityModel<U extends Measurable> implements Serializable {
    private static final long serialVersionUID = 1L;

    private double value;
    private U unit;

    public QuantityModel() {
    }

    public QuantityModel(double value, U unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public U getUnit() {
        return unit;
    }

    public void setUnit(U unit) {
        this.unit = unit;
    }

    public MeasurementType getMeasurementType() {
        return unit != null ? unit.getMeasurementType() : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QuantityModel)) {
            return false;
        }
        QuantityModel<?> other = (QuantityModel<?>) obj;
        return Double.compare(value, other.value) == 0 && Objects.equals(unit, other.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }

    @Override
    public String toString() {
        return String.format("QuantityModel{value=%.2f, unit=%s}", value, unit != null ? unit.getUnitName() : null);
    }
}