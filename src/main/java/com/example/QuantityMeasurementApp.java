package com.example;

import com.example.controller.QuantityMeasurementController;
import com.example.domain.LengthUnit;
import com.example.domain.Measurable;
import com.example.domain.Quantity;
import com.example.domain.QuantityTemperature;
import com.example.domain.TemperatureUnit;
import com.example.domain.WeightUnit;
import com.example.dto.QuantityDTO;
import com.example.factory.QuantityMeasurementFactory;

public class QuantityMeasurementApp {
    private static final double EPSILON = 1e-6;
    private static final QuantityMeasurementApp INSTANCE = new QuantityMeasurementApp();

    private final QuantityMeasurementController controller;

    private QuantityMeasurementApp() {
        this.controller = QuantityMeasurementFactory.createController();
    }

    public static QuantityMeasurementApp getInstance() {
        return INSTANCE;
    }

    public QuantityMeasurementController getController() {
        return controller;
    }

    public static <U extends Measurable> double convert(double value, U source, U target) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Value must be a finite number.");
        }
        if (source == null) {
            throw new IllegalArgumentException("Source unit must not be null.");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target unit must not be null.");
        }

        Quantity<U> quantity = new Quantity<>(value, source);
        return quantity.convertTo(target).getValue();
    }

    public static <U extends Measurable> double add(double value1, U unit1, double value2, U unit2, U resultUnit) {
        if (Double.isNaN(value1) || Double.isInfinite(value1)) {
            throw new IllegalArgumentException("Value 1 must be a finite number.");
        }
        if (Double.isNaN(value2) || Double.isInfinite(value2)) {
            throw new IllegalArgumentException("Value 2 must be a finite number.");
        }
        if (unit1 == null) {
            throw new IllegalArgumentException("Unit 1 must not be null.");
        }
        if (unit2 == null) {
            throw new IllegalArgumentException("Unit 2 must not be null.");
        }
        if (resultUnit == null) {
            throw new IllegalArgumentException("Result unit must not be null.");
        }

        Quantity<U> quantity1 = new Quantity<>(value1, unit1);
        Quantity<U> quantity2 = new Quantity<>(value2, unit2);
        return quantity1.add(quantity2, resultUnit).getValue();
    }

    public static <U extends Measurable> Quantity<U> add(Quantity<U> quantity1, Quantity<U> quantity2, U resultUnit) {
        if (quantity1 == null) {
            throw new IllegalArgumentException("First quantity must not be null.");
        }
        if (quantity2 == null) {
            throw new IllegalArgumentException("Second quantity must not be null.");
        }
        if (resultUnit == null) {
            throw new IllegalArgumentException("Result unit must not be null.");
        }

        return quantity1.add(quantity2, resultUnit);
    }

    public static <U extends Measurable> Quantity<U> add(Quantity<U> quantity1, Quantity<U> quantity2) {
        if (quantity1 == null) {
            throw new IllegalArgumentException("First quantity must not be null.");
        }
        if (quantity2 == null) {
            throw new IllegalArgumentException("Second quantity must not be null.");
        }

        return quantity1.add(quantity2);
    }

    public static <U extends Measurable> void demonstrateEquality(Quantity<U> quantity1, Quantity<U> quantity2) {
        if (quantity1 == null || quantity2 == null) {
            throw new IllegalArgumentException("Both quantities must not be null.");
        }

        boolean isEqual = quantity1.equals(quantity2);
        System.out.printf("%s equals %s: %s%n", quantity1, quantity2, isEqual);
    }

    public static <U extends Measurable> void demonstrateEquality(double value1, U unit1, double value2, U unit2) {
        Quantity<U> quantity1 = new Quantity<>(value1, unit1);
        Quantity<U> quantity2 = new Quantity<>(value2, unit2);
        demonstrateEquality(quantity1, quantity2);
    }

    public static <U extends Measurable> void demonstrateConversion(Quantity<U> quantity, U targetUnit) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity must not be null.");
        }
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit must not be null.");
        }

        Quantity<U> converted = quantity.convertTo(targetUnit);
        System.out.printf("Converting %s to %s: %.6f%n", quantity, targetUnit.getUnitName(), converted.getValue());
    }

    public static <U extends Measurable> void demonstrateConversion(double value, U fromUnit, U toUnit) {
        double result = convert(value, fromUnit, toUnit);
        System.out.printf("Converting %.2f %s to %s: %.6f%n", value, fromUnit.getUnitName(), toUnit.getUnitName(),
                result);
    }

    public static <U extends Measurable> void demonstrateAddition(Quantity<U> quantity1, Quantity<U> quantity2) {
        if (quantity1 == null || quantity2 == null) {
            throw new IllegalArgumentException("Both quantities must not be null.");
        }

        Quantity<U> result = quantity1.add(quantity2);
        System.out.printf("Adding %s + %s = %s%n", quantity1, quantity2, result);
    }

    public static <U extends Measurable> void demonstrateAddition(Quantity<U> quantity1, Quantity<U> quantity2,
            U resultUnit) {
        if (quantity1 == null || quantity2 == null) {
            throw new IllegalArgumentException("Both quantities must not be null.");
        }
        if (resultUnit == null) {
            throw new IllegalArgumentException("Result unit must not be null.");
        }

        Quantity<U> result = quantity1.add(quantity2, resultUnit);
        System.out.printf("Adding %s + %s = %s%n", quantity1, quantity2, result);
    }

    public static <U extends Measurable> void demonstrateAddition(double value1, U unit1, double value2, U unit2,
            U resultUnit) {
        double result = add(value1, unit1, value2, unit2, resultUnit);
        System.out.printf("Adding %.2f %s + %.2f %s = %.6f %s%n", value1, unit1.getUnitName(), value2,
                unit2.getUnitName(), result, resultUnit.getUnitName());
    }

    public static <U extends Measurable> Quantity<U> subtract(Quantity<U> quantity1, Quantity<U> quantity2) {
        if (quantity1 == null) {
            throw new IllegalArgumentException("First quantity must not be null.");
        }
        if (quantity2 == null) {
            throw new IllegalArgumentException("Second quantity must not be null.");
        }

        return quantity1.subtract(quantity2);
    }

    public static <U extends Measurable> Quantity<U> subtract(Quantity<U> quantity1, Quantity<U> quantity2,
            U resultUnit) {
        if (quantity1 == null) {
            throw new IllegalArgumentException("First quantity must not be null.");
        }
        if (quantity2 == null) {
            throw new IllegalArgumentException("Second quantity must not be null.");
        }
        if (resultUnit == null) {
            throw new IllegalArgumentException("Result unit must not be null.");
        }

        return quantity1.subtract(quantity2, resultUnit);
    }

    public static <U extends Measurable> void demonstrateSubtraction(Quantity<U> quantity1, Quantity<U> quantity2) {
        if (quantity1 == null || quantity2 == null) {
            throw new IllegalArgumentException("Both quantities must not be null.");
        }

        Quantity<U> result = quantity1.subtract(quantity2);
        System.out.printf("Subtracting %s - %s = %s%n", quantity1, quantity2, result);
    }

    public static <U extends Measurable> void demonstrateSubtraction(Quantity<U> quantity1, Quantity<U> quantity2,
            U resultUnit) {
        if (quantity1 == null || quantity2 == null) {
            throw new IllegalArgumentException("Both quantities must not be null.");
        }
        if (resultUnit == null) {
            throw new IllegalArgumentException("Result unit must not be null.");
        }

        Quantity<U> result = quantity1.subtract(quantity2, resultUnit);
        System.out.printf("Subtracting %s - %s = %s%n", quantity1, quantity2, result);
    }

    public static <U extends Measurable> Quantity<U> divide(Quantity<U> quantity1, Quantity<U> quantity2) {
        if (quantity1 == null) {
            throw new IllegalArgumentException("First quantity must not be null.");
        }
        if (quantity2 == null) {
            throw new IllegalArgumentException("Second quantity must not be null.");
        }

        return quantity1.divide(quantity2);
    }

    public static <U extends Measurable> Quantity<U> divide(Quantity<U> quantity1, Quantity<U> quantity2,
            U resultUnit) {
        if (quantity1 == null) {
            throw new IllegalArgumentException("First quantity must not be null.");
        }
        if (quantity2 == null) {
            throw new IllegalArgumentException("Second quantity must not be null.");
        }
        if (resultUnit == null) {
            throw new IllegalArgumentException("Result unit must not be null.");
        }

        return quantity1.divide(quantity2, resultUnit);
    }

    public static <U extends Measurable> void demonstrateDivision(Quantity<U> quantity1, Quantity<U> quantity2) {
        if (quantity1 == null || quantity2 == null) {
            throw new IllegalArgumentException("Both quantities must not be null.");
        }

        Quantity<U> result = quantity1.divide(quantity2);
        System.out.printf("Dividing %s / %s = %s%n", quantity1, quantity2, result);
    }

    public static <U extends Measurable> void demonstrateDivision(Quantity<U> quantity1, Quantity<U> quantity2,
            U resultUnit) {
        if (quantity1 == null || quantity2 == null) {
            throw new IllegalArgumentException("Both quantities must not be null.");
        }
        if (resultUnit == null) {
            throw new IllegalArgumentException("Result unit must not be null.");
        }

        Quantity<U> result = quantity1.divide(quantity2, resultUnit);
        System.out.printf("Dividing %s / %s = %s%n", quantity1, quantity2, result);
    }

    // ===== BACKWARD COMPATIBILITY METHODS FOR LENGTH =====
    // These methods maintain API compatibility with UC1-UC8

    public static double convert(double value, LengthUnit source, LengthUnit target) {
        return QuantityMeasurementApp.convert(value, (Measurable) source, (Measurable) target);
    }

    public static double add(double value1, LengthUnit unit1, double value2, LengthUnit unit2, LengthUnit resultUnit) {
        return QuantityMeasurementApp.<LengthUnit>add(value1, unit1, value2, unit2, resultUnit);
    }

    public static void demonstrateLengthConversion(double value, LengthUnit fromUnit, LengthUnit toUnit) {
        demonstrateConversion(value, fromUnit, toUnit);
    }

    public static void demonstrateLengthConversion(Quantity<LengthUnit> quantity, LengthUnit targetUnit) {
        demonstrateConversion(quantity, targetUnit);
    }

    public static void demonstrateLengthEquality(Quantity<LengthUnit> length1, Quantity<LengthUnit> length2) {
        demonstrateEquality(length1, length2);
    }

    public static void demonstrateLengthComparison(double value1, LengthUnit unit1, double value2, LengthUnit unit2) {
        demonstrateEquality(value1, unit1, value2, unit2);
    }

    public static void demonstrateLengthAddition(double value1, LengthUnit unit1, double value2, LengthUnit unit2,
            LengthUnit resultUnit) {
        demonstrateAddition(value1, unit1, value2, unit2, resultUnit);
    }

    public static void demonstrateLengthAddition(Quantity<LengthUnit> length1, Quantity<LengthUnit> length2,
            LengthUnit resultUnit) {
        demonstrateAddition(length1, length2, resultUnit);
    }

    public static void demonstrateLengthAddition(Quantity<LengthUnit> length1, Quantity<LengthUnit> length2) {
        demonstrateAddition(length1, length2);
    }

    public static boolean checkFeetEquality(double value1, double value2) {
        Quantity<LengthUnit> feet1 = new Quantity<>(value1, LengthUnit.FEET);
        Quantity<LengthUnit> feet2 = new Quantity<>(value2, LengthUnit.FEET);
        return feet1.equals(feet2);
    }

    public static boolean checkInchesEquality(double value1, double value2) {
        Quantity<LengthUnit> inches1 = new Quantity<>(value1, LengthUnit.INCH);
        Quantity<LengthUnit> inches2 = new Quantity<>(value2, LengthUnit.INCH);
        return inches1.equals(inches2);
    }

    public static boolean checkYardEquality(double value1, double value2) {
        Quantity<LengthUnit> yard1 = new Quantity<>(value1, LengthUnit.YARD);
        Quantity<LengthUnit> yard2 = new Quantity<>(value2, LengthUnit.YARD);
        return yard1.equals(yard2);
    }

    public static boolean checkCentimeterEquality(double value1, double value2) {
        Quantity<LengthUnit> cm1 = new Quantity<>(value1, LengthUnit.CENTIMETER);
        Quantity<LengthUnit> cm2 = new Quantity<>(value2, LengthUnit.CENTIMETER);
        return cm1.equals(cm2);
    }

    // ===== BACKWARD COMPATIBILITY METHODS FOR WEIGHT =====
    // These methods maintain API compatibility with UC9

    public static double convertWeight(double value, WeightUnit source, WeightUnit target) {
        return QuantityMeasurementApp.convert(value, source, target);
    }

    public static double addWeight(double value1, WeightUnit unit1, double value2, WeightUnit unit2,
            WeightUnit resultUnit) {
        return QuantityMeasurementApp.<WeightUnit>add(value1, unit1, value2, unit2, resultUnit);
    }

    public static void demonstrateWeightConversion(double value, WeightUnit fromUnit, WeightUnit toUnit) {
        demonstrateConversion(value, fromUnit, toUnit);
    }

    public static void demonstrateWeightConversion(Quantity<WeightUnit> quantity, WeightUnit targetUnit) {
        demonstrateConversion(quantity, targetUnit);
    }

    public static void demonstrateWeightEquality(Quantity<WeightUnit> weight1, Quantity<WeightUnit> weight2) {
        demonstrateEquality(weight1, weight2);
    }

    public static void demonstrateWeightComparison(double value1, WeightUnit unit1, double value2, WeightUnit unit2) {
        demonstrateEquality(value1, unit1, value2, unit2);
    }

    public static void demonstrateWeightAddition(double value1, WeightUnit unit1, double value2, WeightUnit unit2,
            WeightUnit resultUnit) {
        demonstrateAddition(value1, unit1, value2, unit2, resultUnit);
    }

    public static void demonstrateWeightAddition(Quantity<WeightUnit> weight1, Quantity<WeightUnit> weight2,
            WeightUnit resultUnit) {
        demonstrateAddition(weight1, weight2, resultUnit);
    }

    public static void demonstrateWeightAddition(Quantity<WeightUnit> weight1, Quantity<WeightUnit> weight2) {
        demonstrateAddition(weight1, weight2);
    }

    public static boolean checkKilogramEquality(double value1, double value2) {
        Quantity<WeightUnit> kg1 = new Quantity<>(value1, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> kg2 = new Quantity<>(value2, WeightUnit.KILOGRAM);
        return kg1.equals(kg2);
    }

    public static boolean checkGramEquality(double value1, double value2) {
        Quantity<WeightUnit> g1 = new Quantity<>(value1, WeightUnit.GRAM);
        Quantity<WeightUnit> g2 = new Quantity<>(value2, WeightUnit.GRAM);
        return g1.equals(g2);
    }

    public static boolean checkPoundEquality(double value1, double value2) {
        Quantity<WeightUnit> lb1 = new Quantity<>(value1, WeightUnit.POUND);
        Quantity<WeightUnit> lb2 = new Quantity<>(value2, WeightUnit.POUND);
        return lb1.equals(lb2);
    }

    // ===== TEMPERATURE SUPPORT METHODS (UC14) =====
    // These methods demonstrate temperature with selective arithmetic support

    public static double convertTemperature(double value, TemperatureUnit source, TemperatureUnit target) {
        return QuantityMeasurementApp.convert(value, source, target);
    }

    public static void demonstrateTemperatureEquality(QuantityTemperature temp1, QuantityTemperature temp2) {
        if (temp1 == null || temp2 == null) {
            throw new IllegalArgumentException("Both temperatures must not be null.");
        }
        boolean isEqual = temp1.equals(temp2);
        System.out.printf("  Temperature Equality: %s equals %s: %s%n", temp1, temp2, isEqual);
    }

    public static void demonstrateTemperatureConversion(QuantityTemperature temp, TemperatureUnit targetUnit) {
        if (temp == null || targetUnit == null) {
            throw new IllegalArgumentException("Both temperature and target unit must not be null.");
        }
        QuantityTemperature converted = temp.convertTo(targetUnit);
        System.out.printf("  Temperature Conversion: %s → %s = %s%n", temp, targetUnit.getUnitName(), converted);
    }

    public static void demonstrateTemperatureSubtraction(QuantityTemperature temp1, QuantityTemperature temp2) {
        if (temp1 == null || temp2 == null) {
            throw new IllegalArgumentException("Both temperatures must not be null.");
        }
        QuantityTemperature difference = temp1.subtract(temp2);
        System.out.printf("  Temperature Difference: %s - %s = %s%n", temp1, temp2, difference);
    }

    public static void demonstrateUnsupportedTemperatureAddition(QuantityTemperature temp1, QuantityTemperature temp2) {
        if (temp1 == null || temp2 == null) {
            throw new IllegalArgumentException("Both temperatures must not be null.");
        }
        try {
            temp1.add(temp2);
            System.out.printf("  ERROR: Should have thrown UnsupportedOperationException for addition!%n");
        } catch (UnsupportedOperationException e) {
            System.out.printf("  Unsupported Addition: %s + %s throws: %s%n",
                    temp1, temp2, e.getMessage());
        }
    }

    public static void demonstrateUnsupportedTemperatureDivision(QuantityTemperature temp1, QuantityTemperature temp2) {
        if (temp1 == null || temp2 == null) {
            throw new IllegalArgumentException("Both temperatures must not be null.");
        }
        try {
            temp1.divide(temp2);
            System.out.printf("  ERROR: Should have thrown UnsupportedOperationException for division!%n");
        } catch (UnsupportedOperationException e) {
            System.out.printf("  Unsupported Division: %s / %s throws: %s%n",
                    temp1, temp2, e.getMessage());
        }
    }

    public static void demonstrateUnsupportedTemperatureMultiplication(QuantityTemperature temp, double scalar) {
        if (temp == null) {
            throw new IllegalArgumentException("Temperature must not be null.");
        }
        try {
            temp.multiply(scalar);
            System.out.printf("  ERROR: Should have thrown UnsupportedOperationException for multiplication!%n");
        } catch (UnsupportedOperationException e) {
            System.out.printf("  Unsupported Multiplication: %s * %f throws: %s%n",
                    temp, scalar, e.getMessage());
        }
    }

    public static void demonstrateTemperatureCrossCategorySafety() {
        QuantityTemperature celsius = new QuantityTemperature(25.0, TemperatureUnit.CELSIUS);
        Quantity<LengthUnit> feet = new Quantity<>(5.0, LengthUnit.FEET);

        boolean isEqual = celsius.equals(feet);
        System.out.printf("  Cross-Category Type Safety: %s equals %s: %s (should be false)%n",
                celsius, feet, isEqual);
    }

        private void runSampleWorkflow() {
        System.out.println("QUANTITY MEASUREMENT APPLICATION\n");

        QuantityDTO length = new QuantityDTO(5.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO lengthConversion = controller.performConversion(length, QuantityDTO.LengthUnit.INCH);
        System.out.printf("Length Conversion: %s -> %s%n", length, lengthConversion);

        QuantityDTO weightSum = controller.performAddition(
            new QuantityDTO(3.0, QuantityDTO.WeightUnit.KILOGRAM),
            new QuantityDTO(500.0, QuantityDTO.WeightUnit.GRAM),
            QuantityDTO.WeightUnit.KILOGRAM);
        System.out.printf("Weight Addition: %s%n", weightSum);

        boolean temperatureEquality = controller.performComparison(
            new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.CELSIUS),
            new QuantityDTO(32.0, QuantityDTO.TemperatureUnit.FAHRENHEIT));
        System.out.printf("Temperature Comparison: %s%n", temperatureEquality);

        QuantityDTO temperatureDifference = controller.performSubtraction(
            new QuantityDTO(212.0, QuantityDTO.TemperatureUnit.FAHRENHEIT),
            new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.CELSIUS),
            QuantityDTO.TemperatureUnit.CELSIUS);
        System.out.printf("Temperature Difference: %s%n", temperatureDifference);
        }

    public static void main(String[] args) {
        getInstance().runSampleWorkflow();
    }
}