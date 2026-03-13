package com.example.domain;

import java.util.Locale;

public enum LengthUnit implements Measurable {
    FEET(1.0, "FEET"),
    INCH(1.0 / 12.0, "INCH"),
    YARD(3.0, "YARD"),
    CENTIMETER((0.393701) / 12.0, "CENTIMETER"),
    METER(3.28084, "METER");

    private final double conversionFactor;
    private final String unitName;
    // Lambda expression: LengthUnit supports arithmetic operations
    private final SupportsArithmetic supportsArithmetic = () -> true;

    LengthUnit(double conversionFactor, String unitName) {
        this.conversionFactor = conversionFactor;
        this.unitName = unitName;
    }

    @Override
    public double getConversionFactor() {
        return conversionFactor;
    }

    public double toFeetFactor() {
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
        return MeasurementType.LENGTH;
    }

    @Override
    public Measurable getUnitByName(String unitName) {
        return fromUnitName(unitName);
    }

    public static LengthUnit fromUnitName(String unitName) {
        if (unitName == null || unitName.trim().isEmpty()) {
            throw new IllegalArgumentException("Length unit name must not be blank.");
        }
        return LengthUnit.valueOf(unitName.trim().toUpperCase(Locale.ROOT));
    }
    
    @Override
    public SupportsArithmetic getSupportsArithmetic() {
        return supportsArithmetic;
    }
}
