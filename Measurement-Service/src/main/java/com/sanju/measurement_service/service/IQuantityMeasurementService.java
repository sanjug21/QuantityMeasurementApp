package com.sanju.measurement_service.service;

import com.sanju.measurement_service.dto.OperationRequestDTO;
import com.sanju.measurement_service.dto.QuantityDTO;
import com.sanju.measurement_service.dto.QuantityOperationResultDTO;
import com.sanju.measurement_service.entity.QuantityMeasurementEntity;
import java.util.List;

public interface IQuantityMeasurementService {
    QuantityOperationResultDTO convert(QuantityDTO source, String targetUnit);

    QuantityOperationResultDTO compare(QuantityDTO firstQuantity, QuantityDTO secondQuantity);

    QuantityOperationResultDTO add(QuantityDTO firstQuantity, QuantityDTO secondQuantity, String resultUnit);

    QuantityOperationResultDTO subtract(QuantityDTO firstQuantity, QuantityDTO secondQuantity, String resultUnit);

    QuantityOperationResultDTO multiply(QuantityDTO firstQuantity, QuantityDTO secondQuantity, String resultUnit);

    QuantityOperationResultDTO divide(QuantityDTO firstQuantity, QuantityDTO secondQuantity, String resultUnit);

    QuantityOperationResultDTO operate(OperationRequestDTO request);

    List<QuantityMeasurementEntity> getMeasurementHistoryForCurrentUser();

    List<QuantityMeasurementEntity> getMeasurementHistoryByOperationForCurrentUser(String operationType);
}
