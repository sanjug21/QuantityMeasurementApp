package com.example.repository;

import com.example.dto.QuantityDTO;
import com.example.entity.QuantityMeasurementEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public interface IQuantityMeasurementRepository {
    QuantityMeasurementEntity save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> findAll();

    List<QuantityMeasurementEntity> getAllMeasurements();

    Optional<QuantityMeasurementEntity> findById(String id);

    void clear();

    default List<QuantityMeasurementEntity> getMeasurementsByOperation(String operationType) {
        if (operationType == null || operationType.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String normalizedOperationType = operationType.trim().toUpperCase(Locale.ROOT);
        List<QuantityMeasurementEntity> filteredEntities = new ArrayList<QuantityMeasurementEntity>();
        for (QuantityMeasurementEntity entity : getAllMeasurements()) {
            if (normalizedOperationType.equals(entity.getOperationType())) {
                filteredEntities.add(entity);
            }
        }
        return filteredEntities;
    }

    default List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
        if (measurementType == null || measurementType.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String normalizedMeasurementType = measurementType.trim().toUpperCase(Locale.ROOT);
        List<QuantityMeasurementEntity> filteredEntities = new ArrayList<QuantityMeasurementEntity>();

        for (QuantityMeasurementEntity entity : getAllMeasurements()) {
            if (hasMeasurementType(entity.getFirstOperand(), normalizedMeasurementType)
                    || hasMeasurementType(entity.getSecondOperand(), normalizedMeasurementType)
                    || hasMeasurementType(entity.getResult(), normalizedMeasurementType)) {
                filteredEntities.add(entity);
            }
        }

        return filteredEntities;
    }

    default long getTotalCount() {
        return getAllMeasurements().size();
    }

    default void deleteAll() {
        clear();
    }

    default String getPoolStatistics() {
        return "Connection pool statistics are not available for this repository implementation.";
    }

    default void releaseResources() {
        // Default no-op for repositories that do not manage external resources.
    }

    static boolean hasMeasurementType(QuantityDTO quantityDTO, String measurementType) {
        if (quantityDTO == null) {
            return false;
        }

        String quantityMeasurement = quantityDTO.getMeasurement();
        return quantityMeasurement != null
                && Objects.equals(quantityMeasurement.toUpperCase(Locale.ROOT), measurementType);
    }
}