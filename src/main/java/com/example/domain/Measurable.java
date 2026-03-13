package com.example.domain;

import java.util.Objects;

public interface Measurable {
 
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
    MeasurementType getMeasurementType();
    Measurable getUnitByName(String unitName);

    default Measurable getUnit(String unitName) {
        return getUnitByName(unitName);
    }

    default String getMeasurement() {
        return getMeasurementType().name();
    }

    default boolean isCompatibleWith(Measurable other) {
        return other != null && getMeasurementType() == other.getMeasurementType();
    }

    static Measurable from(String measurementType, String unitName) {
        return from(MeasurementType.from(measurementType), unitName);
    }

    static Measurable from(MeasurementType measurementType, String unitName) {
        Objects.requireNonNull(measurementType, "Measurement type must not be null.");
        switch (measurementType) {
            case LENGTH:
                return LengthUnit.fromUnitName(unitName);
            case WEIGHT:
                return WeightUnit.fromUnitName(unitName);
            case VOLUME:
                return VolumeUnit.fromUnitName(unitName);
            case TEMPERATURE:
                return TemperatureUnit.fromUnitName(unitName);
            default:
                throw new IllegalArgumentException("Unsupported measurement type: " + measurementType);
        }
    }
    
    default SupportsArithmetic getSupportsArithmetic() {
        return () -> true;  // Default: all units support arithmetic
    }
    
    default void validateOperationSupport(String operation) {
        if (!getSupportsArithmetic().isSupported()) {
            throw new UnsupportedOperationException(
                String.format("Arithmetic operation '%s' is not supported for unit '%s'",
                              operation, getUnitName())
            );
        }
    }
}
