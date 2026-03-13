package com.example.repository;

import com.example.entity.QuantityMeasurementEntity;
import java.util.List;
import java.util.Optional;

public interface IQuantityMeasurementRepository {
    QuantityMeasurementEntity save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> findAll();

    List<QuantityMeasurementEntity> getAllMeasurements();

    Optional<QuantityMeasurementEntity> findById(String id);

    void clear();
}