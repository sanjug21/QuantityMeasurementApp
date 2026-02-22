package com.example;

/**
 * Interface for measurement units.
 * All unit enums should implement this interface to standardize unit behavior
 * across different measurement categories (length, weight, volume, etc.).
 */
public interface Measurable {
    /**
     * Returns the conversion factor relative to the base unit.
     * For example, in LengthUnit: FEET returns 1.0, INCH returns 1.0/12.0, YARD returns 3.0
     * 
     * @return the conversion factor to the base unit
     */
    double getConversionFactor();

    /**
     * Converts a value in this unit to the base unit.
     * 
     * @param value the value in this unit
     * @return the value converted to the base unit
     */
    double convertToBaseUnit(double value);

    /**
     * Converts a value from the base unit to this unit.
     * 
     * @param baseValue the value in the base unit
     * @return the value converted to this unit
     */
    double convertFromBaseUnit(double baseValue);

    /**
     * Returns a readable name for this unit.
     * 
     * @return the unit name (e.g., "FEET", "INCHES", "KILOGRAM")
     */
    String getUnitName();
}
