package com.example.domain;

import java.util.Locale;
import java.util.function.Function;

public enum TemperatureUnit implements Measurable {
    CELSIUS(273.15, "CELSIUS") {
        // Function to convert Celsius to Kelvin (base unit)
        private final Function<Double, Double> celsiusToKelvin = (celsius) -> celsius + 273.15;
        
        // Function to convert Kelvin to Celsius
        private final Function<Double, Double> kelvinToCelsius = (kelvin) -> kelvin - 273.15;
        
        @Override
        public double convertToBaseUnit(double value) {
            return celsiusToKelvin.apply(value);
        }
        
        @Override
        public double convertFromBaseUnit(double baseValue) {
            return kelvinToCelsius.apply(baseValue);
        }
    },
    FAHRENHEIT(459.67, "FAHRENHEIT") {
        // Function to convert Fahrenheit to Kelvin (base unit)
        private final Function<Double, Double> fahrenheitToKelvin = 
            (fahrenheit) -> (fahrenheit + 459.67) * (5.0 / 9.0);
        
        // Function to convert Kelvin to Fahrenheit
        private final Function<Double, Double> kelvinToFahrenheit = 
            (kelvin) -> kelvin * (9.0 / 5.0) - 459.67;
        
        @Override
        public double convertToBaseUnit(double value) {
            return fahrenheitToKelvin.apply(value);
        }
        
        @Override
        public double convertFromBaseUnit(double baseValue) {
            return kelvinToFahrenheit.apply(baseValue);
        }
    },
    KELVIN(1.0, "KELVIN") {
        // Kelvin is the base unit - identity function for both directions
        private final Function<Double, Double> kelvinToKelvin = (kelvin) -> kelvin;
        
        @Override
        public double convertToBaseUnit(double value) {
            return kelvinToKelvin.apply(value);
        }
        
        @Override
        public double convertFromBaseUnit(double baseValue) {
            return kelvinToKelvin.apply(baseValue);
        }
    };

    private final double conversionFactor;
    private final String unitName;
    // Lambda expression: TemperatureUnit does NOT support arithmetic operations
    private final SupportsArithmetic supportsArithmetic = () -> false;

    TemperatureUnit(double conversionFactor, String unitName) {
        this.conversionFactor = conversionFactor;
        this.unitName = unitName;
    }

    @Override
    public double getConversionFactor() {
        return conversionFactor;
    }

    @Override
    public String getUnitName() {
        return unitName;
    }

    @Override
    public MeasurementType getMeasurementType() {
        return MeasurementType.TEMPERATURE;
    }

    @Override
    public Measurable getUnitByName(String unitName) {
        return fromUnitName(unitName);
    }

    public static TemperatureUnit fromUnitName(String unitName) {
        if (unitName == null || unitName.trim().isEmpty()) {
            throw new IllegalArgumentException("Temperature unit name must not be blank.");
        }
        return TemperatureUnit.valueOf(unitName.trim().toUpperCase(Locale.ROOT));
    }
    
    @Override
    public SupportsArithmetic getSupportsArithmetic() {
        return supportsArithmetic;
    }
    
    @Override
    public void validateOperationSupport(String operation) {
        switch (operation.toUpperCase()) {
            case "DIVIDE":
            case "MULTIPLY":
                throw new UnsupportedOperationException(
                    String.format("Operation '%s' is meaningless for temperature. " +
                                "Multiplication and division do not apply to absolute temperatures.",
                                operation)
                );
            case "ADD":
                throw new UnsupportedOperationException(
                    "Cannot add two absolute temperatures. " +
                    "For example, 100°C + 50°C ≠ 150°C in a meaningful sense. " +
                    "Consider using subtraction to calculate a temperature difference."
                );
            case "SUBTRACT":
                // Subtraction is allowed to calculate temperature differences
                // No exception thrown
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format("Unknown operation '%s' for temperature unit '%s'",
                                  operation, getUnitName())
                );
        }
    }
}
