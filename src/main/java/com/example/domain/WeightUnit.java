package com.example.domain;

import java.util.Locale;

public enum WeightUnit implements Measurable {
    KILOGRAM(1.0, "KILOGRAM"),
    GRAM(0.001, "GRAM"),
    POUND(0.453592, "POUND"),
    OUNCE(0.0283495, "OUNCE");

    private final double conversionFactor;
    private final String unitName;
    // Lambda expression: WeightUnit supports arithmetic operations
    private final SupportsArithmetic supportsArithmetic = () -> true;

    WeightUnit(double conversionFactor, String unitName) {
        this.conversionFactor = conversionFactor;
        this.unitName = unitName;
    }

    @Override
    public double getConversionFactor() {
        return conversionFactor;
    }

    public double toKilogramFactor() {
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
        return MeasurementType.WEIGHT;
    }

    @Override
    public Measurable getUnitByName(String unitName) {
        return fromUnitName(unitName);
    }

    public static WeightUnit fromUnitName(String unitName) {
        if (unitName == null || unitName.trim().isEmpty()) {
            throw new IllegalArgumentException("Weight unit name must not be blank.");
        }
        return WeightUnit.valueOf(unitName.trim().toUpperCase(Locale.ROOT));
    }
    
    @Override
    public SupportsArithmetic getSupportsArithmetic() {
        return supportsArithmetic;
    }
}
