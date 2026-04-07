package com.sanju.measurement_service.repository;

import com.sanju.measurement_service.entity.QuantityMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IQuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, String> {
    List<QuantityMeasurementEntity> findAllByOrderByCreatedAtAsc();

    List<QuantityMeasurementEntity> findByOperationTypeOrderByCreatedAtAsc(String operationType);

    List<QuantityMeasurementEntity> findByUserIdOrderByCreatedAtAsc(Long userId);

    List<QuantityMeasurementEntity> findByUserIdAndOperationTypeOrderByCreatedAtAsc(Long userId, String operationType);
}
