package com.example.controller;

import com.example.dto.QuantityDTO;
import com.example.entity.QuantityMeasurementEntity;
import com.example.service.IQuantityMeasurementService;
import java.util.List;
import java.util.Objects;

public class QuantityMeasurementController {
    private final IQuantityMeasurementService quantityMeasurementService;

    public QuantityMeasurementController(IQuantityMeasurementService quantityMeasurementService) {
        this.quantityMeasurementService = Objects.requireNonNull(
                quantityMeasurementService,
                "QuantityMeasurementService must not be null.");
    }

    public QuantityDTO performConversion(QuantityDTO source, QuantityDTO targetDefinition) {
        return quantityMeasurementService.convert(source, targetDefinition);
    }

    public QuantityDTO performConversion(QuantityDTO source, QuantityDTO.IMeasurableUnit targetUnit) {
        return quantityMeasurementService.convert(source, targetUnit);
    }

    public boolean performComparison(QuantityDTO firstQuantity, QuantityDTO secondQuantity) {
        return quantityMeasurementService.compare(firstQuantity, secondQuantity);
    }

    public QuantityDTO performAddition(QuantityDTO firstQuantity, QuantityDTO secondQuantity) {
        return quantityMeasurementService.add(firstQuantity, secondQuantity);
    }

    public QuantityDTO performAddition(QuantityDTO firstQuantity, QuantityDTO secondQuantity,
            QuantityDTO.IMeasurableUnit resultUnit) {
        return quantityMeasurementService.add(firstQuantity, secondQuantity, resultUnit);
    }

    public QuantityDTO performSubtraction(QuantityDTO firstQuantity, QuantityDTO secondQuantity) {
        return quantityMeasurementService.subtract(firstQuantity, secondQuantity);
    }

    public QuantityDTO performSubtraction(QuantityDTO firstQuantity, QuantityDTO secondQuantity,
            QuantityDTO.IMeasurableUnit resultUnit) {
        return quantityMeasurementService.subtract(firstQuantity, secondQuantity, resultUnit);
    }

    public QuantityDTO performDivision(QuantityDTO firstQuantity, QuantityDTO secondQuantity) {
        return quantityMeasurementService.divide(firstQuantity, secondQuantity);
    }

    public QuantityDTO performDivision(QuantityDTO firstQuantity, QuantityDTO secondQuantity,
            QuantityDTO.IMeasurableUnit resultUnit) {
        return quantityMeasurementService.divide(firstQuantity, secondQuantity, resultUnit);
    }

    public List<QuantityMeasurementEntity> getMeasurementHistory() {
        return quantityMeasurementService.getMeasurementHistory();
    }
}