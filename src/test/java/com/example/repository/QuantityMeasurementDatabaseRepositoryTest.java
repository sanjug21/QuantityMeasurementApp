package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dto.QuantityDTO;
import com.example.entity.QuantityMeasurementEntity;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class QuantityMeasurementDatabaseRepositoryTest {
    private QuantityMeasurementDatabaseRepository repository;

    @BeforeEach
    public void setUp() {
        System.setProperty("app.repository.type", "database");
        System.setProperty("app.datasource.driverClassName", "org.h2.Driver");
        System.setProperty("app.datasource.url", "jdbc:h2:mem:qm_test_" + System.nanoTime() + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        System.setProperty("app.datasource.username", "sa");
        System.setProperty("app.datasource.password", "");
        System.setProperty("app.datasource.pool.initialSize", "1");
        System.setProperty("app.datasource.pool.maxSize", "4");
        System.setProperty("app.datasource.pool.connectionTimeoutMillis", "2000");
        System.setProperty("app.datasource.schemaLocation", "db/schema.sql");

        repository = new QuantityMeasurementDatabaseRepository();
        repository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        if (repository != null) {
            repository.deleteAll();
            repository.releaseResources();
        }

        System.clearProperty("app.repository.type");
        System.clearProperty("app.datasource.driverClassName");
        System.clearProperty("app.datasource.url");
        System.clearProperty("app.datasource.username");
        System.clearProperty("app.datasource.password");
        System.clearProperty("app.datasource.pool.initialSize");
        System.clearProperty("app.datasource.pool.maxSize");
        System.clearProperty("app.datasource.pool.connectionTimeoutMillis");
        System.clearProperty("app.datasource.schemaLocation");
    }

    @Test
    @DisplayName("Database repository saves and retrieves measurement entities")
    public void testSaveAndFindAll() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                "ADD",
                new QuantityDTO(2.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(500.0, QuantityDTO.WeightUnit.GRAM),
                new QuantityDTO(2.5, QuantityDTO.WeightUnit.KILOGRAM));

        repository.save(entity);

        List<QuantityMeasurementEntity> savedEntities = repository.findAll();
        assertEquals(1, savedEntities.size());
        assertEquals("ADD", savedEntities.get(0).getOperationType());
        assertTrue(savedEntities.get(0).isSuccessful());
    }

    @Test
    @DisplayName("Database repository supports query by operation type")
    public void testGetMeasurementsByOperation() {
        repository.save(new QuantityMeasurementEntity(
                "COMPARE",
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH),
                true));
        repository.save(new QuantityMeasurementEntity(
                "CONVERT",
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.METER),
                new QuantityDTO(100.0, QuantityDTO.LengthUnit.CENTIMETER)));

        List<QuantityMeasurementEntity> compareResults = repository.getMeasurementsByOperation("COMPARE");

        assertEquals(1, compareResults.size());
        assertEquals("COMPARE", compareResults.get(0).getOperationType());
    }

    @Test
    @DisplayName("Database repository supports query by measurement type")
    public void testGetMeasurementsByType() {
        repository.save(new QuantityMeasurementEntity(
                "ADD",
                new QuantityDTO(2.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(500.0, QuantityDTO.WeightUnit.GRAM),
                new QuantityDTO(2.5, QuantityDTO.WeightUnit.KILOGRAM)));
        repository.save(new QuantityMeasurementEntity(
                "CONVERT",
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.METER),
                new QuantityDTO(100.0, QuantityDTO.LengthUnit.CENTIMETER)));

        List<QuantityMeasurementEntity> weightResults = repository.getMeasurementsByType("WEIGHT");

        assertEquals(1, weightResults.size());
        assertEquals("WEIGHT", weightResults.get(0).getFirstOperand().getMeasurement());
    }

    @Test
    @DisplayName("Database repository reports total count and supports delete all")
    public void testGetTotalCountAndDeleteAll() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                "DIVIDE",
                new QuantityDTO(10.0, QuantityDTO.VolumeUnit.LITRE),
                new QuantityDTO(2.0, QuantityDTO.VolumeUnit.LITRE),
                new QuantityDTO(5.0, QuantityDTO.VolumeUnit.LITRE));

        repository.save(entity);
        assertEquals(1, repository.getTotalCount());

        assertFalse(repository.findById(entity.getId()).isEmpty());
        repository.deleteAll();

        assertEquals(0, repository.getTotalCount());
    }
}
