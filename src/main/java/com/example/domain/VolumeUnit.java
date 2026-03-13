package com.example.domain;

import java.util.Locale;

public enum VolumeUnit implements Measurable {
    LITRE(1.0, "LITRE"),
    MILLILITRE(0.001, "MILLILITRE"),
    GALLON(3.78541, "GALLON");

    private final double conversionFactor;
    private final String unitName;
    // Lambda expression: VolumeUnit supports arithmetic operations
    private final SupportsArithmetic supportsArithmetic = () -> true;

    VolumeUnit(double conversionFactor, String unitName) {
        this.conversionFactor = conversionFactor;
        this.unitName = unitName;
    }

    @Override
    public double getConversionFactor() {
        return conversionFactor;
    }

    public double toLitreFactor() {
        return conversionFactor;
    }

    @Override
    public double convertToBaseUnit(double value) {
        return value * conversionFactor;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / conversionFactor;
    }

    @Override
    public String getUnitName() {
        return unitName;
    }

    @Override
    public MeasurementType getMeasurementType() {
        return MeasurementType.VOLUME;
    }

    @Override
    public Measurable getUnitByName(String unitName) {
        return fromUnitName(unitName);
    }

    public static VolumeUnit fromUnitName(String unitName) {
        if (unitName == null || unitName.trim().isEmpty()) {
            throw new IllegalArgumentException("Volume unit name must not be blank.");
        }
        return VolumeUnit.valueOf(unitName.trim().toUpperCase(Locale.ROOT));
    }
    
    @Override
    public SupportsArithmetic getSupportsArithmetic() {
        return supportsArithmetic;
    }
}
