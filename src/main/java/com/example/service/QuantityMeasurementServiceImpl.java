package com.example.service;

import com.example.domain.LengthUnit;
import com.example.domain.Measurable;
import com.example.domain.MeasurementType;
import com.example.domain.Quantity;
import com.example.domain.TemperatureUnit;
import com.example.domain.VolumeUnit;
import com.example.domain.WeightUnit;
import com.example.dto.QuantityDTO;
import com.example.entity.QuantityMeasurementEntity;
import com.example.exception.QuantityMeasurementException;
import com.example.model.QuantityModel;
import com.example.repository.IQuantityMeasurementRepository;
import java.util.List;
import java.util.Objects;

public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {
    private static final String CONVERT = "CONVERT";
    private static final String COMPARE = "COMPARE";
    private static final String ADD = "ADD";
    private static final String SUBTRACT = "SUBTRACT";
    private static final String DIVIDE = "DIVIDE";

    private final IQuantityMeasurementRepository repository;

    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
        this.repository = Objects.requireNonNull(repository, "Repository must not be null.");
    }

    @Override
    public QuantityDTO convert(QuantityDTO source, QuantityDTO targetDefinition) {
        QuantityDTO sanitizedTarget = requireQuantityDefinition(targetDefinition, "Target quantity definition must not be null.");
        return convert(source, sanitizedTarget.getUnit());
    }

    @Override
    public QuantityDTO convert(QuantityDTO source, QuantityDTO.IMeasurableUnit targetUnit) {
        QuantityDTO sanitizedSource = requireQuantityDefinition(source, "Source quantity must not be null.");
        QuantityDTO.IMeasurableUnit sanitizedTargetUnit = requireUnit(targetUnit, "Target unit must not be null.");

        try {
            MeasurementType measurementType = sanitizedSource.getMeasurementType();
            validateMatchingMeasurementType(measurementType, sanitizedTargetUnit.getMeasurementType(), CONVERT);
            Measurable domainTargetUnit = toDomainUnit(sanitizedTargetUnit);
            QuantityDTO result = convertByType(measurementType, sanitizedSource, domainTargetUnit);
            repository.save(new QuantityMeasurementEntity(CONVERT, sanitizedSource, result));
            return result;
        } catch (RuntimeException exception) {
            throw wrapException(CONVERT, sanitizedSource, null, exception);
        }
    }

    @Override
    public boolean compare(QuantityDTO firstQuantity, QuantityDTO secondQuantity) {
        QuantityDTO sanitizedFirst = requireQuantityDefinition(firstQuantity, "First quantity must not be null.");
        QuantityDTO sanitizedSecond = requireQuantityDefinition(secondQuantity, "Second quantity must not be null.");

        try {
            MeasurementType measurementType = validateMatchingMeasurementType(
                    sanitizedFirst.getMeasurementType(),
                    sanitizedSecond.getMeasurementType(),
                    COMPARE);
            boolean result = compareByType(measurementType, sanitizedFirst, sanitizedSecond);
            repository.save(new QuantityMeasurementEntity(COMPARE, sanitizedFirst, sanitizedSecond, result));
            return result;
        } catch (RuntimeException exception) {
            throw wrapException(COMPARE, sanitizedFirst, sanitizedSecond, exception);
        }
    }

    @Override
    public QuantityDTO add(QuantityDTO firstQuantity, QuantityDTO secondQuantity) {
        QuantityDTO sanitizedFirst = requireQuantityDefinition(firstQuantity, "First quantity must not be null.");
        return add(sanitizedFirst, secondQuantity, sanitizedFirst.getUnit());
    }

    @Override
    public QuantityDTO add(QuantityDTO firstQuantity, QuantityDTO secondQuantity, QuantityDTO.IMeasurableUnit resultUnit) {
        return performBinaryOperation(ADD, firstQuantity, secondQuantity, resultUnit);
    }

    @Override
    public QuantityDTO subtract(QuantityDTO firstQuantity, QuantityDTO secondQuantity) {
        QuantityDTO sanitizedFirst = requireQuantityDefinition(firstQuantity, "First quantity must not be null.");
        return subtract(sanitizedFirst, secondQuantity, sanitizedFirst.getUnit());
    }

    @Override
    public QuantityDTO subtract(QuantityDTO firstQuantity, QuantityDTO secondQuantity,
            QuantityDTO.IMeasurableUnit resultUnit) {
        return performBinaryOperation(SUBTRACT, firstQuantity, secondQuantity, resultUnit);
    }

    @Override
    public QuantityDTO divide(QuantityDTO firstQuantity, QuantityDTO secondQuantity) {
        QuantityDTO sanitizedFirst = requireQuantityDefinition(firstQuantity, "First quantity must not be null.");
        return divide(sanitizedFirst, secondQuantity, sanitizedFirst.getUnit());
    }

    @Override
    public QuantityDTO divide(QuantityDTO firstQuantity, QuantityDTO secondQuantity,
            QuantityDTO.IMeasurableUnit resultUnit) {
        return performBinaryOperation(DIVIDE, firstQuantity, secondQuantity, resultUnit);
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementHistory() {
        return repository.getAllMeasurements();
    }

    private QuantityDTO performBinaryOperation(String operationType, QuantityDTO firstQuantity, QuantityDTO secondQuantity,
            QuantityDTO.IMeasurableUnit resultUnit) {
        QuantityDTO sanitizedFirst = requireQuantityDefinition(firstQuantity, "First quantity must not be null.");
        QuantityDTO sanitizedSecond = requireQuantityDefinition(secondQuantity, "Second quantity must not be null.");
        QuantityDTO.IMeasurableUnit sanitizedResultUnit = requireUnit(resultUnit, "Result unit must not be null.");

        try {
            MeasurementType measurementType = validateMatchingMeasurementType(
                    sanitizedFirst.getMeasurementType(),
                    sanitizedSecond.getMeasurementType(),
                    operationType);
            validateMatchingMeasurementType(measurementType, sanitizedResultUnit.getMeasurementType(), operationType);

            Measurable domainResultUnit = toDomainUnit(sanitizedResultUnit);
            QuantityDTO result = applyBinaryOperationByType(operationType, measurementType, sanitizedFirst, sanitizedSecond,
                    domainResultUnit);
            repository.save(new QuantityMeasurementEntity(operationType, sanitizedFirst, sanitizedSecond, result));
            return result;
        } catch (RuntimeException exception) {
            throw wrapException(operationType, sanitizedFirst, sanitizedSecond, exception);
        }
    }

    private QuantityDTO requireQuantityDefinition(QuantityDTO quantityDTO, String message) {
        if (quantityDTO == null) {
            throw new QuantityMeasurementException(message);
        }

        QuantityDTO copy = new QuantityDTO(quantityDTO);
        if (copy.getUnit() == null) {
            throw new QuantityMeasurementException("Quantity unit must not be null.");
        }
        if (copy.getMeasurementType() == null) {
            copy.setMeasurementType(copy.getUnit().getMeasurementType());
        }
        validateMatchingMeasurementType(copy.getMeasurementType(), copy.getUnit().getMeasurementType(), "VALIDATE");
        return copy;
    }

    private QuantityDTO.IMeasurableUnit requireUnit(QuantityDTO.IMeasurableUnit unit, String message) {
        if (unit == null) {
            throw new QuantityMeasurementException(message);
        }
        return unit;
    }

    private MeasurementType validateMatchingMeasurementType(MeasurementType firstMeasurementType,
            MeasurementType secondMeasurementType, String operationType) {
        if (firstMeasurementType == null || secondMeasurementType == null) {
            throw new QuantityMeasurementException("Measurement type must not be null.");
        }
        if (firstMeasurementType != secondMeasurementType) {
            throw new QuantityMeasurementException(String.format(
                    "Operation '%s' requires compatible measurement types but found '%s' and '%s'.",
                    operationType,
                    firstMeasurementType,
                    secondMeasurementType));
        }
        return firstMeasurementType;
    }

    private QuantityDTO convertByType(MeasurementType measurementType, QuantityDTO source, Measurable targetUnit) {
        switch (measurementType) {
            case LENGTH:
                return convertTyped(toModel(source, LengthUnit.class), (LengthUnit) targetUnit);
            case WEIGHT:
                return convertTyped(toModel(source, WeightUnit.class), (WeightUnit) targetUnit);
            case VOLUME:
                return convertTyped(toModel(source, VolumeUnit.class), (VolumeUnit) targetUnit);
            case TEMPERATURE:
                return convertTyped(toModel(source, TemperatureUnit.class), (TemperatureUnit) targetUnit);
            default:
                throw new QuantityMeasurementException("Unsupported measurement type: " + measurementType);
        }
    }

    private boolean compareByType(MeasurementType measurementType, QuantityDTO firstQuantity, QuantityDTO secondQuantity) {
        switch (measurementType) {
            case LENGTH:
                return compareTyped(toModel(firstQuantity, LengthUnit.class), toModel(secondQuantity, LengthUnit.class));
            case WEIGHT:
                return compareTyped(toModel(firstQuantity, WeightUnit.class), toModel(secondQuantity, WeightUnit.class));
            case VOLUME:
                return compareTyped(toModel(firstQuantity, VolumeUnit.class), toModel(secondQuantity, VolumeUnit.class));
            case TEMPERATURE:
                return compareTyped(toModel(firstQuantity, TemperatureUnit.class),
                        toModel(secondQuantity, TemperatureUnit.class));
            default:
                throw new QuantityMeasurementException("Unsupported measurement type: " + measurementType);
        }
    }

    private QuantityDTO applyBinaryOperationByType(String operationType, MeasurementType measurementType,
            QuantityDTO firstQuantity, QuantityDTO secondQuantity, Measurable resultUnit) {
        switch (measurementType) {
            case LENGTH:
                return applyBinaryOperation(operationType, toModel(firstQuantity, LengthUnit.class),
                        toModel(secondQuantity, LengthUnit.class), (LengthUnit) resultUnit);
            case WEIGHT:
                return applyBinaryOperation(operationType, toModel(firstQuantity, WeightUnit.class),
                        toModel(secondQuantity, WeightUnit.class), (WeightUnit) resultUnit);
            case VOLUME:
                return applyBinaryOperation(operationType, toModel(firstQuantity, VolumeUnit.class),
                        toModel(secondQuantity, VolumeUnit.class), (VolumeUnit) resultUnit);
            case TEMPERATURE:
                return applyBinaryOperation(operationType, toModel(firstQuantity, TemperatureUnit.class),
                        toModel(secondQuantity, TemperatureUnit.class), (TemperatureUnit) resultUnit);
            default:
                throw new QuantityMeasurementException("Unsupported measurement type: " + measurementType);
        }
    }

    private <U extends Measurable> QuantityDTO convertTyped(QuantityModel<U> sourceModel, U targetUnit) {
        Quantity<U> converted = new Quantity<U>(sourceModel.getValue(), sourceModel.getUnit()).convertTo(targetUnit);
        return toDto(converted.getValue(), converted.getUnit());
    }

    private <U extends Measurable> boolean compareTyped(QuantityModel<U> firstModel, QuantityModel<U> secondModel) {
        Quantity<U> firstQuantity = new Quantity<U>(firstModel.getValue(), firstModel.getUnit());
        Quantity<U> secondQuantity = new Quantity<U>(secondModel.getValue(), secondModel.getUnit());
        return firstQuantity.equals(secondQuantity);
    }

    private <U extends Measurable> QuantityDTO applyBinaryOperation(String operationType, QuantityModel<U> firstModel,
            QuantityModel<U> secondModel, U resultUnit) {
        Quantity<U> firstQuantity = new Quantity<U>(firstModel.getValue(), firstModel.getUnit());
        Quantity<U> secondQuantity = new Quantity<U>(secondModel.getValue(), secondModel.getUnit());
        Quantity<U> result;

        if (ADD.equals(operationType)) {
            result = firstQuantity.add(secondQuantity, resultUnit);
        } else if (SUBTRACT.equals(operationType)) {
            result = firstQuantity.subtract(secondQuantity, resultUnit);
        } else if (DIVIDE.equals(operationType)) {
            result = firstQuantity.divide(secondQuantity, resultUnit);
        } else {
            throw new QuantityMeasurementException("Unsupported operation type: " + operationType);
        }

        return toDto(result.getValue(), result.getUnit());
    }

    private <U extends Measurable> QuantityModel<U> toModel(QuantityDTO quantityDTO, Class<U> unitType) {
        Measurable unit = Measurable.from(quantityDTO.getMeasurementType(), quantityDTO.getUnitName());
        return new QuantityModel<U>(quantityDTO.getValue(), unitType.cast(unit));
    }

    private Measurable toDomainUnit(QuantityDTO.IMeasurableUnit unit) {
        return Measurable.from(unit.getMeasurementType(), unit.getUnitName());
    }

    private QuantityDTO toDto(double value, Measurable unit) {
        return new QuantityDTO(value, toDtoUnit(unit));
    }

    private QuantityDTO.IMeasurableUnit toDtoUnit(Measurable unit) {
        switch (unit.getMeasurementType()) {
            case LENGTH:
                return QuantityDTO.LengthUnit.fromUnitName(unit.getUnitName());
            case WEIGHT:
                return QuantityDTO.WeightUnit.fromUnitName(unit.getUnitName());
            case VOLUME:
                return QuantityDTO.VolumeUnit.fromUnitName(unit.getUnitName());
            case TEMPERATURE:
                return QuantityDTO.TemperatureUnit.fromUnitName(unit.getUnitName());
            default:
                throw new QuantityMeasurementException("Unsupported measurement type: " + unit.getMeasurementType());
        }
    }

    private QuantityMeasurementException wrapException(String operationType, QuantityDTO firstQuantity,
            QuantityDTO secondQuantity, RuntimeException exception) {
        String message = exception.getMessage() != null
                ? exception.getMessage()
                : String.format("Operation '%s' failed.", operationType);

        try {
            repository.save(new QuantityMeasurementEntity(operationType, firstQuantity, secondQuantity, message));
        } catch (RuntimeException repositoryException) {
            exception.addSuppressed(repositoryException);
        }

        if (exception instanceof QuantityMeasurementException) {
            return (QuantityMeasurementException) exception;
        }
        return new QuantityMeasurementException(message, exception);
    }
}