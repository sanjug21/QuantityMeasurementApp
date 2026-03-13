package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.controller.QuantityMeasurementController;
import com.example.domain.LengthUnit;
import com.example.domain.Measurable;
import com.example.domain.MeasurementType;
import com.example.domain.WeightUnit;
import com.example.dto.QuantityDTO;
import com.example.entity.QuantityMeasurementEntity;
import com.example.exception.QuantityMeasurementException;
import com.example.factory.QuantityMeasurementFactory;
import com.example.repository.IQuantityMeasurementRepository;
import com.example.repository.QuantityMeasurementCacheRepository;
import com.example.service.IQuantityMeasurementService;
import com.example.service.QuantityMeasurementServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class QuantityMeasurementLayeredArchitectureTest {
    private static final double EPSILON = 1e-6;

    @AfterEach
    public void cleanRepository() {
        QuantityMeasurementCacheRepository.getInstance().clear();
    }

    @Test
    @DisplayName("Measurable helpers resolve measurement type and unit name")
    public void testMeasurableHelpersResolveUnitNames() {
        assertEquals(MeasurementType.LENGTH, LengthUnit.METER.getMeasurementType());
        assertSame(LengthUnit.METER, LengthUnit.FEET.getUnitByName("meter"));
        assertSame(WeightUnit.OUNCE, Measurable.from("weight", "ounce"));
        assertThrows(IllegalArgumentException.class, () -> Measurable.from("length", "gram"));
    }

    @Test
    @DisplayName("QuantityDTO resolves units and validates measurement alignment")
    public void testQuantityDtoResolutionAndValidation() {
        QuantityDTO dto = new QuantityDTO(2.5, "length", "meter");

        assertEquals(2.5, dto.getValue(), EPSILON);
        assertEquals(MeasurementType.LENGTH, dto.getMeasurementType());
        assertEquals("METER", dto.getUnitName());

        assertThrows(IllegalArgumentException.class,
                () -> new QuantityDTO(1.0, MeasurementType.LENGTH, QuantityDTO.WeightUnit.GRAM));
    }

    @Test
    @DisplayName("Service converts DTO quantities and stores successful history")
    public void testServiceConvertStoresHistory() {
        InMemoryQuantityMeasurementRepository repository = new InMemoryQuantityMeasurementRepository();
        IQuantityMeasurementService service = new QuantityMeasurementServiceImpl(repository);

        QuantityDTO result = service.convert(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.METER),
                QuantityDTO.LengthUnit.FEET);

        assertEquals(3.28, result.getValue(), 0.01);
        assertEquals("FEET", result.getUnitName());
        assertEquals(1, repository.getAllMeasurements().size());
        assertEquals("CONVERT", repository.getAllMeasurements().get(0).getOperationType());
        assertTrue(repository.getAllMeasurements().get(0).isSuccessful());
    }

    @Test
    @DisplayName("Service rejects cross-category comparison and stores error entity")
    public void testServiceRejectsCrossCategoryComparison() {
        InMemoryQuantityMeasurementRepository repository = new InMemoryQuantityMeasurementRepository();
        IQuantityMeasurementService service = new QuantityMeasurementServiceImpl(repository);

        QuantityMeasurementException exception = assertThrows(QuantityMeasurementException.class,
                () -> service.compare(
                        new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                        new QuantityDTO(1.0, QuantityDTO.WeightUnit.GRAM)));

        assertTrue(exception.getMessage().contains("compatible measurement types"));
        assertEquals(1, repository.getAllMeasurements().size());
        assertFalse(repository.getAllMeasurements().get(0).isSuccessful());
        assertEquals("COMPARE", repository.getAllMeasurements().get(0).getOperationType());
    }

    @Test
    @DisplayName("QuantityMeasurementEntity exposes defensive copies of DTO fields")
    public void testEntityReturnsDefensiveCopies() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                "CONVERT",
                new QuantityDTO(5.0, QuantityDTO.VolumeUnit.LITRE),
                new QuantityDTO(5000.0, QuantityDTO.VolumeUnit.MILLILITRE));

        QuantityDTO firstOperand = entity.getFirstOperand();
        firstOperand.setValue(100.0);

        assertEquals(5.0, entity.getFirstOperand().getValue(), EPSILON);
        assertTrue(entity.isSuccessful());
    }

    @Test
    @DisplayName("Controller performs layered operations through injected service")
    public void testControllerUsesLayeredService() {
        InMemoryQuantityMeasurementRepository repository = new InMemoryQuantityMeasurementRepository();
        QuantityMeasurementController controller = QuantityMeasurementFactory.createController(repository);

        QuantityDTO result = controller.performSubtraction(
                new QuantityDTO(212.0, QuantityDTO.TemperatureUnit.FAHRENHEIT),
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.CELSIUS),
                QuantityDTO.TemperatureUnit.CELSIUS);

        assertEquals(100.0, result.getValue(), 0.01);
        assertEquals("CELSIUS", result.getUnitName());
        assertEquals(1, controller.getMeasurementHistory().size());
    }

    @Test
    @DisplayName("Cache repository stores and returns measurement entities")
    public void testCacheRepositoryStoresEntities() {
        QuantityMeasurementCacheRepository repository = QuantityMeasurementCacheRepository.getInstance();
        repository.clear();

        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                "ADD",
                new QuantityDTO(2.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(500.0, QuantityDTO.WeightUnit.GRAM),
                new QuantityDTO(2.5, QuantityDTO.WeightUnit.KILOGRAM));

        repository.save(entity);

        assertEquals(1, repository.findAll().size());
        assertTrue(repository.findById(entity.getId()).isPresent());
    }

    private static class InMemoryQuantityMeasurementRepository implements IQuantityMeasurementRepository {
        private final List<QuantityMeasurementEntity> store = new ArrayList<QuantityMeasurementEntity>();

        @Override
        public QuantityMeasurementEntity save(QuantityMeasurementEntity entity) {
            store.add(entity);
            return entity;
        }

        @Override
        public List<QuantityMeasurementEntity> findAll() {
            return new ArrayList<QuantityMeasurementEntity>(store);
        }

        @Override
        public List<QuantityMeasurementEntity> getAllMeasurements() {
            return findAll();
        }

        @Override
        public Optional<QuantityMeasurementEntity> findById(String id) {
            for (QuantityMeasurementEntity entity : store) {
                if (entity.getId().equals(id)) {
                    return Optional.of(entity);
                }
            }
            return Optional.empty();
        }

        @Override
        public void clear() {
            store.clear();
        }
    }
}