package com.example.domain;

import java.util.Locale;

public enum MeasurementType {
    LENGTH,
    WEIGHT,
    VOLUME,
    TEMPERATURE;

    public static MeasurementType from(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Measurement type must not be blank.");
        }
        return MeasurementType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}