package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class QuantityMeasurementAppTest {
    private static final double EPSILON = 1e-6;

    // ===== UC14: TEMPERATURE TEST CASES - SELECTIVE ARITHMETIC SUPPORT =====

    @Test
    @DisplayName("Temperature: Equality - 0°C equals 273.15 K")
    public void testTemperatureEquality_CelsiusToKelvin() {
        QuantityTemperature celsius = new QuantityTemperature(0.0, TemperatureUnit.CELSIUS);
        QuantityTemperature kelvin = new QuantityTemperature(273.15, TemperatureUnit.KELVIN);
        assertEquals(celsius, kelvin);
    }

    @Test
    @DisplayName("Temperature: Equality - 0°C equals 32°F")
    public void testTemperatureEquality_CelsiusToFahrenheit() {
        QuantityTemperature celsius = new QuantityTemperature(0.0, TemperatureUnit.CELSIUS);
        QuantityTemperature fahrenheit = new QuantityTemperature(32.0, TemperatureUnit.FAHRENHEIT);
        assertEquals(celsius, fahrenheit);
    }

    @Test
    @DisplayName("Temperature: Equality - 100°C equals 212°F")
    public void testTemperatureEquality_BoilingPointEquivalence() {
        QuantityTemperature celsius = new QuantityTemperature(100.0, TemperatureUnit.CELSIUS);
        QuantityTemperature fahrenheit = new QuantityTemperature(212.0, TemperatureUnit.FAHRENHEIT);
        assertEquals(celsius, fahrenheit);
    }

    @Test
    @DisplayName("Temperature: Inequality - 25°C ≠ 25°F")
    public void testTemperatureInequality_DifferentValues() {
        QuantityTemperature celsius = new QuantityTemperature(25.0, TemperatureUnit.CELSIUS);
        QuantityTemperature fahrenheit = new QuantityTemperature(25.0, TemperatureUnit.FAHRENHEIT);
        assertNotEquals(celsius, fahrenheit);
    }

    @Test
    @DisplayName("Temperature: Conversion - Celsius to Fahrenheit")
    public void testTemperatureConversion_CelsiusToFahrenheit() {
        QuantityTemperature celsius = new QuantityTemperature(25.0, TemperatureUnit.CELSIUS);
        QuantityTemperature fahrenheit = celsius.convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(77.0, fahrenheit.getValue(), 0.01);
    }

    @Test
    @DisplayName("Temperature: Conversion - Fahrenheit to Celsius")
    public void testTemperatureConversion_FahrenheitToCelsius() {
        QuantityTemperature fahrenheit = new QuantityTemperature(98.6, TemperatureUnit.FAHRENHEIT);
        QuantityTemperature celsius = fahrenheit.convertTo(TemperatureUnit.CELSIUS);
        assertEquals(37.0, celsius.getValue(), 0.01);
    }

    @Test
    @DisplayName("Temperature: Conversion - Celsius to Kelvin")
    public void testTemperatureConversion_CelsiusToKelvin() {
        QuantityTemperature celsius = new QuantityTemperature(-40.0, TemperatureUnit.CELSIUS);
        QuantityTemperature kelvin = celsius.convertTo(TemperatureUnit.KELVIN);
        assertEquals(233.15, kelvin.getValue(), 0.01);
    }

    @Test
    @DisplayName("Temperature: Conversion - Kelvin to Celsius")
    public void testTemperatureConversion_KelvinToCelsius() {
        QuantityTemperature kelvin = new QuantityTemperature(373.15, TemperatureUnit.KELVIN);
        QuantityTemperature celsius = kelvin.convertTo(TemperatureUnit.CELSIUS);
        assertEquals(100.0, celsius.getValue(), 0.01);
    }

    @Test
    @DisplayName("Temperature: Conversion - Fahrenheit to Kelvin")
    public void testTemperatureConversion_FahrenheitToKelvin() {
        QuantityTemperature fahrenheit = new QuantityTemperature(32.0, TemperatureUnit.FAHRENHEIT);
        QuantityTemperature kelvin = fahrenheit.convertTo(TemperatureUnit.KELVIN);
        assertEquals(273.15, kelvin.getValue(), 0.01);
    }

    @Test
    @DisplayName("Temperature: Subtraction - calculates temperature difference")
    public void testTemperatureSubtraction_CalculatesDifference() {
        QuantityTemperature high = new QuantityTemperature(100.0, TemperatureUnit.CELSIUS);
        QuantityTemperature low = new QuantityTemperature(0.0, TemperatureUnit.CELSIUS);
        QuantityTemperature difference = high.subtract(low);
        assertEquals(100.0, difference.getValue(), 0.01);
    }

    @Test
    @DisplayName("Temperature: Subtraction - cross-unit difference")
    public void testTemperatureSubtraction_CrossUnitDifference() {
        QuantityTemperature fahrenheit = new QuantityTemperature(212.0, TemperatureUnit.FAHRENHEIT);
        QuantityTemperature celsius = new QuantityTemperature(0.0, TemperatureUnit.CELSIUS);
        QuantityTemperature difference = fahrenheit.subtract(celsius, TemperatureUnit.CELSIUS);
        assertEquals(100.0, difference.getValue(), 0.01);
    }

    @Test
    @DisplayName("Temperature: Subtraction - same temperature equals zero difference")
    public void testTemperatureSubtraction_SameTemperatureZeroDifference() {
        QuantityTemperature temp1 = new QuantityTemperature(25.0, TemperatureUnit.CELSIUS);
        QuantityTemperature temp2 = new QuantityTemperature(25.0, TemperatureUnit.CELSIUS);
        QuantityTemperature difference = temp1.subtract(temp2);
        assertEquals(0.0, difference.getValue(), EPSILON);
    }

    @Test
    @DisplayName("Temperature: Addition throws UnsupportedOperationException")
    public void testTemperatureAddition_ThrowsException() {
        QuantityTemperature temp1 = new QuantityTemperature(25.0, TemperatureUnit.CELSIUS);
        QuantityTemperature temp2 = new QuantityTemperature(30.0, TemperatureUnit.CELSIUS);
        assertThrows(UnsupportedOperationException.class, () -> temp1.add(temp2));
    }

    @Test
    @DisplayName("Temperature: Division throws UnsupportedOperationException")
    public void testTemperatureDivision_ThrowsException() {
        QuantityTemperature temp1 = new QuantityTemperature(100.0, TemperatureUnit.CELSIUS);
        QuantityTemperature temp2 = new QuantityTemperature(50.0, TemperatureUnit.CELSIUS);
        assertThrows(UnsupportedOperationException.class, () -> temp1.divide(temp2));
    }

    @Test
    @DisplayName("Temperature: Multiplication throws UnsupportedOperationException")
    public void testTemperatureMultiplication_ThrowsException() {
        QuantityTemperature temp = new QuantityTemperature(25.0, TemperatureUnit.CELSIUS);
        assertThrows(UnsupportedOperationException.class, () -> temp.multiply(2.0));
    }

    @Test
    @DisplayName("Temperature: Addition error message is clear")
    public void testTemperatureAddition_ErrorMessageClarity() {
        QuantityTemperature temp1 = new QuantityTemperature(100.0, TemperatureUnit.CELSIUS);
        QuantityTemperature temp2 = new QuantityTemperature(50.0, TemperatureUnit.CELSIUS);
        try {
            temp1.add(temp2);
            assert false : "Should have thrown UnsupportedOperationException";
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("Cannot add two absolute temperatures"));
        }
    }

    @Test
    @DisplayName("Temperature: Division error message is clear")
    public void testTemperatureDivision_ErrorMessageClarity() {
        QuantityTemperature temp1 = new QuantityTemperature(100.0, TemperatureUnit.CELSIUS);
        QuantityTemperature temp2 = new QuantityTemperature(50.0, TemperatureUnit.CELSIUS);
        try {
            temp1.divide(temp2);
            assert false : "Should have thrown UnsupportedOperationException";
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("meaningless"));
        }
    }

    @Test
    @DisplayName("Temperature: Cross-category prevention - Temperature ≠ Length")
    public void testTemperatureCrossCategoryPrevention_TemperatureVsLength() {
        QuantityTemperature celsius = new QuantityTemperature(25.0, TemperatureUnit.CELSIUS);
        Quantity<LengthUnit> feet = new Quantity<>(25.0, LengthUnit.FEET);
        assertNotEquals(celsius, feet);
    }

    @Test
    @DisplayName("Temperature: Using Quantity<TemperatureUnit> - Equality")
    public void testTemperature_UsingQuantityClass_Equality() {
        Quantity<TemperatureUnit> celsius = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> celsius_again = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        assertEquals(celsius, celsius_again);
    }

    @Test
    @DisplayName("Temperature: Using Quantity<TemperatureUnit> - Conversion")
    public void testTemperature_UsingQuantityClass_Conversion() {
        Quantity<TemperatureUnit> celsius = new Quantity<>(25.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> fahrenheit = celsius.convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(77.0, fahrenheit.getValue(), 0.01);
    }

    @Test
    @DisplayName("Temperature: Using Quantity<TemperatureUnit> - Subtraction allowed")
    public void testTemperature_UsingQuantityClass_SubtractionAllowed() {
        Quantity<TemperatureUnit> high = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> low = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> difference = high.subtract(low);
        assertEquals(100.0, difference.getValue(), 0.01);
    }

    @Test
    @DisplayName("Temperature: Using Quantity<TemperatureUnit> - Addition throws exception")
    public void testTemperature_UsingQuantityClass_AdditionThrows() {
        Quantity<TemperatureUnit> temp1 = new Quantity<>(25.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> temp2 = new Quantity<>(30.0, TemperatureUnit.CELSIUS);
        assertThrows(UnsupportedOperationException.class, () -> temp1.add(temp2));
    }

    @Test
    @DisplayName("Temperature: Using Quantity<TemperatureUnit> - Division throws exception")
    public void testTemperature_UsingQuantityClass_DivisionThrows() {
        Quantity<TemperatureUnit> temp1 = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> temp2 = new Quantity<>(50.0, TemperatureUnit.CELSIUS);
        assertThrows(UnsupportedOperationException.class, () -> temp1.divide(temp2));
    }

    @Test
    @DisplayName("Temperature: Null input validation")
    public void testTemperature_NullInputValidation() {
        QuantityTemperature temp = new QuantityTemperature(25.0, TemperatureUnit.CELSIUS);
        assertThrows(IllegalArgumentException.class, () -> temp.convertTo(null));
        assertThrows(IllegalArgumentException.class, () -> temp.subtract(null));
    }

    @Test
    @DisplayName("Temperature: NaN/Infinite value validation")
    public void testTemperature_InvalidValueValidation() {
        assertThrows(IllegalArgumentException.class, () -> new QuantityTemperature(Double.NaN, TemperatureUnit.CELSIUS));
        assertThrows(IllegalArgumentException.class, () -> new QuantityTemperature(Double.POSITIVE_INFINITY, TemperatureUnit.CELSIUS));
    }

    @Test
    @DisplayName("Temperature: String representation format")
    public void testTemperature_StringRepresentation() {
        QuantityTemperature temp = new QuantityTemperature(25.0, TemperatureUnit.CELSIUS);
        String str = temp.toString();
        assertTrue(str.contains("25.00"));
        assertTrue(str.contains("CELSIUS"));
    }

    @Test
    @DisplayName("Temperature: Hash code consistency with equality")
    public void testTemperature_HashCodeConsistency() {
        QuantityTemperature celsius = new QuantityTemperature(0.0, TemperatureUnit.CELSIUS);
        QuantityTemperature kelvin = new QuantityTemperature(273.15, TemperatureUnit.KELVIN);
        assertEquals(celsius, kelvin);
        assertEquals(celsius.hashCode(), kelvin.hashCode());
    }

    @Test
    @DisplayName("Temperature: Precision - 2 decimal places rounding")
    public void testTemperature_PrecisionRounding() {
        QuantityTemperature celsius = new QuantityTemperature(23.333, TemperatureUnit.CELSIUS);
        QuantityTemperature fahrenheit = celsius.convertTo(TemperatureUnit.FAHRENHEIT);
        // Result should be rounded, value should have at most 2 decimal places
        assertEquals(Math.round(fahrenheit.getValue() * 100.0) / 100.0, fahrenheit.getValue(), 0.001);
    }

    @Test
    @DisplayName("Temperature: Negative temperature support")
    public void testTemperature_NegativeTemperature() {
        QuantityTemperature negCelsius = new QuantityTemperature(-40.0, TemperatureUnit.CELSIUS);
        QuantityTemperature fahrenheit = negCelsius.convertTo(TemperatureUnit.FAHRENHEIT);
        // -40°C = -40°F (intersection point)
        assertEquals(-40.0, fahrenheit.getValue(), 0.01);
    }

    @Test
    @DisplayName("Temperature: All unit conversions form consistent cycle")
    public void testTemperature_ConversionCycle() {
        QuantityTemperature original = new QuantityTemperature(25.0, TemperatureUnit.CELSIUS);
        QuantityTemperature toF = original.convertTo(TemperatureUnit.FAHRENHEIT);
        QuantityTemperature toK = toF.convertTo(TemperatureUnit.KELVIN);
        QuantityTemperature backToC = toK.convertTo(TemperatureUnit.CELSIUS);
        assertEquals(original, backToC);
    }
}
