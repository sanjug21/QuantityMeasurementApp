package com.example;

import java.util.Objects;

public class QuantityTemperature {
    private static final double EPSILON = 1e-6;
    private final double value;
    private final TemperatureUnit unit;

    public QuantityTemperature(double value, TemperatureUnit unit) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Value must be a finite number.");
        }
        this.unit = Objects.requireNonNull(unit, "Unit must not be null.");
        this.value = value;
    }

    public QuantityTemperature convertTo(TemperatureUnit targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit must not be null.");
        }
        double valueInBaseUnit = unit.convertToBaseUnit(value);
        double convertedValue = targetUnit.convertFromBaseUnit(valueInBaseUnit);
        // Round to two decimal places for precision
        convertedValue = Math.round(convertedValue * 100.0) / 100.0;
        return new QuantityTemperature(convertedValue, targetUnit);
    }

    public double getValue() {
        return value;
    }

    public TemperatureUnit getUnit() {
        return unit;
    }

    public double toKelvin() {
        return unit.convertToBaseUnit(value);
    }

    public QuantityTemperature subtract(QuantityTemperature other, TemperatureUnit resultUnit) {
        if (other == null) {
            throw new IllegalArgumentException("Other temperature must not be null.");
        }
        if (resultUnit == null) {
            throw new IllegalArgumentException("Result unit must not be null.");
        }
        
        // Validate that subtraction is supported
        this.unit.validateOperationSupport("SUBTRACT");
        
        double thisKelvin = this.toKelvin();
        double otherKelvin = other.toKelvin();
        double differenceKelvin = thisKelvin - otherKelvin;
        
        // For temperature differences, just convert the difference as-is (no offset adjustment needed)
        // Since differences are relative, not absolute
        double resultValue = differenceKelvin;
        if (resultUnit != TemperatureUnit.KELVIN) {
            // Convert from Kelvin difference to target unit difference
            // For Celsius: difference in K = difference in °C
            // For Fahrenheit: difference in K * 9/5 = difference in °F
            if (resultUnit == TemperatureUnit.CELSIUS) {
                resultValue = differenceKelvin; // 1K difference = 1°C difference
            } else if (resultUnit == TemperatureUnit.FAHRENHEIT) {
                resultValue = differenceKelvin * (9.0 / 5.0); // 1K difference = 1.8°F difference
            }
        }
        
        // Round to two decimal places for precision
        resultValue = Math.round(resultValue * 100.0) / 100.0;
        return new QuantityTemperature(resultValue, resultUnit);
    }

    public QuantityTemperature subtract(QuantityTemperature other) {
        return subtract(other, this.unit);
    }

    public QuantityTemperature add(QuantityTemperature other) {
        // Validate that addition is NOT supported
        this.unit.validateOperationSupport("ADD");
        // This line will never be reached due to the exception above
        throw new UnsupportedOperationException(
            "Cannot add two absolute temperatures. " +
            "For example, 100°C + 50°C ≠ 150°C in a meaningful sense."
        );
    }

    public QuantityTemperature divide(QuantityTemperature other) {
        // Validate that division is NOT supported
        this.unit.validateOperationSupport("DIVIDE");
        // This line will never be reached due to the exception above
        throw new UnsupportedOperationException(
            "Division is meaningless for absolute temperatures."
        );
    }

    public QuantityTemperature multiply(double scalar) {
        // Validate that multiplication is NOT supported
        this.unit.validateOperationSupport("MULTIPLY");
        // This line will never be reached due to the exception above
        throw new UnsupportedOperationException(
            "Multiplication is meaningless for absolute temperatures."
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof QuantityTemperature)) {
            return false;
        }
        
        QuantityTemperature other = (QuantityTemperature) obj;
        // Compare values in the base unit (Kelvin)
        return Math.abs(this.toKelvin() - other.toKelvin()) < EPSILON;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(toKelvin());
    }

    @Override
    public String toString() {
        return String.format("%.2f %s", value, unit.getUnitName());
    }
}
