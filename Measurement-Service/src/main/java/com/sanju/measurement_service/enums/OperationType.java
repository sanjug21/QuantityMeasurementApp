package com.sanju.measurement_service.enums;

import com.sanju.measurement_service.exception.QuantityMeasurementException;
import java.util.Locale;

public enum OperationType {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    COMPARE,
    CONVERT;

    public static OperationType from(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new QuantityMeasurementException("Operation type is required.");
        }

        try {
            return OperationType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new QuantityMeasurementException("Invalid operation type: " + value);
        }
    }
}
