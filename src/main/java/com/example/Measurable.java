package com.example;

public interface Measurable {
 
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
    
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
