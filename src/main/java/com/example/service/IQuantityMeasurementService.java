package com.example.service;

import com.example.dto.QuantityDTO;
import com.example.entity.QuantityMeasurementEntity;
import java.util.List;

public interface IQuantityMeasurementService {
    QuantityDTO convert(QuantityDTO source, QuantityDTO targetDefinition);

    QuantityDTO convert(QuantityDTO source, QuantityDTO.IMeasurableUnit targetUnit);

    boolean compare(QuantityDTO firstQuantity, QuantityDTO secondQuantity);

    QuantityDTO add(QuantityDTO firstQuantity, QuantityDTO secondQuantity);

    QuantityDTO add(QuantityDTO firstQuantity, QuantityDTO secondQuantity, QuantityDTO.IMeasurableUnit resultUnit);

    QuantityDTO subtract(QuantityDTO firstQuantity, QuantityDTO secondQuantity);

    QuantityDTO subtract(QuantityDTO firstQuantity, QuantityDTO secondQuantity,
            QuantityDTO.IMeasurableUnit resultUnit);

    QuantityDTO divide(QuantityDTO firstQuantity, QuantityDTO secondQuantity);

    QuantityDTO divide(QuantityDTO firstQuantity, QuantityDTO secondQuantity, QuantityDTO.IMeasurableUnit resultUnit);

    List<QuantityMeasurementEntity> getMeasurementHistory();
}