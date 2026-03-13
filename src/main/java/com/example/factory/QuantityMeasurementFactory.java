package com.example.factory;

import com.example.controller.QuantityMeasurementController;
import com.example.repository.IQuantityMeasurementRepository;
import com.example.repository.QuantityMeasurementCacheRepository;
import com.example.service.IQuantityMeasurementService;
import com.example.service.QuantityMeasurementServiceImpl;

public final class QuantityMeasurementFactory {
    private QuantityMeasurementFactory() {
    }

    public static IQuantityMeasurementRepository createRepository() {
        return QuantityMeasurementCacheRepository.getInstance();
    }

    public static IQuantityMeasurementService createService() {
        return createService(createRepository());
    }

    public static IQuantityMeasurementService createService(IQuantityMeasurementRepository repository) {
        return new QuantityMeasurementServiceImpl(repository);
    }

    public static QuantityMeasurementController createController() {
        return createController(createService());
    }

    public static QuantityMeasurementController createController(IQuantityMeasurementRepository repository) {
        return createController(createService(repository));
    }

    public static QuantityMeasurementController createController(IQuantityMeasurementService service) {
        return new QuantityMeasurementController(service);
    }
}