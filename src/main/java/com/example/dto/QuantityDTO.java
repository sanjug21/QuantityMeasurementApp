package com.example.dto;

import com.example.domain.MeasurementType;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

public class QuantityDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private double value;
    private MeasurementType measurementType;
    private IMeasurableUnit unit;

    public interface IMeasurableUnit extends Serializable {
        String getUnitName();

        MeasurementType getMeasurementType();

        default String getMeasurement() {
            return getMeasurementType().name();
        }
    }

    public enum LengthUnit implements IMeasurableUnit {
        FEET,
        INCH,
        YARD,
        CENTIMETER,
        METER;

        @Override
        public String getUnitName() {
            return name();
        }

        @Override
        public MeasurementType getMeasurementType() {
            return MeasurementType.LENGTH;
        }

        public static LengthUnit fromUnitName(String unitName) {
            return LengthUnit.valueOf(unitName.trim().toUpperCase(Locale.ROOT));
        }
    }

    public enum WeightUnit implements IMeasurableUnit {
        KILOGRAM,
        GRAM,
        POUND,
        OUNCE;

        @Override
        public String getUnitName() {
            return name();
        }

        @Override
        public MeasurementType getMeasurementType() {
            return MeasurementType.WEIGHT;
        }

        public static WeightUnit fromUnitName(String unitName) {
            return WeightUnit.valueOf(unitName.trim().toUpperCase(Locale.ROOT));
        }
    }

    public enum VolumeUnit implements IMeasurableUnit {
        LITRE,
        MILLILITRE,
        GALLON;

        @Override
        public String getUnitName() {
            return name();
        }

        @Override
        public MeasurementType getMeasurementType() {
            return MeasurementType.VOLUME;
        }

        public static VolumeUnit fromUnitName(String unitName) {
            return VolumeUnit.valueOf(unitName.trim().toUpperCase(Locale.ROOT));
        }
    }

    public enum TemperatureUnit implements IMeasurableUnit {
        CELSIUS,
        FAHRENHEIT,
        KELVIN;

        @Override
        public String getUnitName() {
            return name();
        }

        @Override
        public MeasurementType getMeasurementType() {
            return MeasurementType.TEMPERATURE;
        }

        public static TemperatureUnit fromUnitName(String unitName) {
            return TemperatureUnit.valueOf(unitName.trim().toUpperCase(Locale.ROOT));
        }
    }

    public QuantityDTO() {
    }

    public QuantityDTO(double value, IMeasurableUnit unit) {
        this(value, unit != null ? unit.getMeasurementType() : null, unit);
    }

    public QuantityDTO(double value, MeasurementType measurementType, IMeasurableUnit unit) {
        this.value = value;
        this.measurementType = measurementType;
        this.unit = unit;
        validateMeasurementAlignment();
    }

    public QuantityDTO(double value, String measurement, IMeasurableUnit unit) {
        this(value, MeasurementType.from(measurement), unit);
    }

    public QuantityDTO(double value, String measurement, String unitName) {
        this(value, MeasurementType.from(measurement), resolveUnit(measurement, unitName));
    }

    public QuantityDTO(QuantityDTO other) {
        this(Objects.requireNonNull(other, "QuantityDTO must not be null.").value, other.measurementType, other.unit);
    }

    public static IMeasurableUnit resolveUnit(String measurement, String unitName) {
        return resolveUnit(MeasurementType.from(measurement), unitName);
    }

    public static IMeasurableUnit resolveUnit(MeasurementType measurementType, String unitName) {
        if (measurementType == null) {
            throw new IllegalArgumentException("Measurement type must not be null.");
        }
        if (unitName == null || unitName.trim().isEmpty()) {
            throw new IllegalArgumentException("Unit name must not be blank.");
        }
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(MeasurementType measurementType) {
        this.measurementType = measurementType;
        validateMeasurementAlignment();
    }

    public String getMeasurement() {
        return measurementType != null ? measurementType.name() : null;
    }

    public void setMeasurement(String measurement) {
        this.measurementType = MeasurementType.from(measurement);
        validateMeasurementAlignment();
    }

    public IMeasurableUnit getUnit() {
        return unit;
    }

    public void setUnit(IMeasurableUnit unit) {
        this.unit = unit;
        validateMeasurementAlignment();
    }

    public void setUnit(String unitName) {
        if (measurementType == null) {
            throw new IllegalArgumentException("Measurement type must be set before unit resolution.");
        }
        this.unit = resolveUnit(measurementType, unitName);
    }

    public String getUnitName() {
        return unit != null ? unit.getUnitName() : null;
    }

    private void validateMeasurementAlignment() {
        if (measurementType != null && unit != null && measurementType != unit.getMeasurementType()) {
            throw new IllegalArgumentException(String.format(
                    "Measurement '%s' does not match unit '%s'.",
                    measurementType,
                    unit.getUnitName()));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QuantityDTO)) {
            return false;
        }
        QuantityDTO other = (QuantityDTO) obj;
        return Double.compare(value, other.value) == 0
                && measurementType == other.measurementType
                && unit == other.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, measurementType, unit);
    }

    @Override
    public String toString() {
        return String.format("QuantityDTO{value=%.2f, measurement=%s, unit=%s}",
                value,
                getMeasurement(),
                getUnitName());
    }
}