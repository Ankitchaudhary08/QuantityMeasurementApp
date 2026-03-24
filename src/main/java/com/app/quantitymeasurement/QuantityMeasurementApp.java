package com.app.quantitymeasurement;

import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.service.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.util.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * QuantityMeasurementApp — Application entry point for UC16.
 * Selects repository (cache or database) based on application.properties.
 */
public class QuantityMeasurementApp {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementApp.class);

    private final IQuantityMeasurementRepository repository;
    private final QuantityMeasurementController controller;

    public QuantityMeasurementApp() {
        ApplicationConfig config = ApplicationConfig.getInstance();
        String repoType = config.getRepositoryType();

        if ("database".equalsIgnoreCase(repoType)) {
            this.repository = new QuantityMeasurementDatabaseRepository();
            logger.info("Using DatabaseRepository (H2 / JDBC)");
        } else {
            this.repository = QuantityMeasurementCacheRepository.getInstance();
            logger.info("Using CacheRepository (in-memory)");
        }

        QuantityMeasurementServiceImpl service = new QuantityMeasurementServiceImpl(repository);
        this.controller = new QuantityMeasurementController(service);
    }

    public void run() {

        logger.info("=== Length Operations ===");
        controller.performComparison(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH));
        controller.performConversion(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0, QuantityDTO.LengthUnit.INCH));
        controller.performAddition(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH));

        logger.info("=== Weight Operations ===");
        controller.performComparison(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM));
        controller.performAddition(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM));
        controller.performDivision(
                new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(5.0, QuantityDTO.WeightUnit.KILOGRAM));

        logger.info("=== Temperature Operations ===");
        controller.performComparison(
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(32.0, QuantityDTO.TemperatureUnit.FAHRENHEIT));
        controller.performConversion(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(0, QuantityDTO.TemperatureUnit.FAHRENHEIT));

        logger.info("=== Cross-Category Prevention ===");
        controller.performComparison(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(100.0, QuantityDTO.LengthUnit.FEET));

        logger.info("=== Repository Stats ===");
        logger.info("Total measurements stored: {}", repository.getTotalCount());
        logger.info("Pool stats: {}", repository.getPoolStatistics());

        List<QuantityMeasurementEntity> all = repository.getAllMeasurements();
        logger.info("All stored measurements ({}):", all.size());
        all.forEach(e -> logger.info("  {}", e));
    }

    public void deleteAllMeasurements() {
        repository.deleteAll();
        logger.info("All measurements deleted from repository");
    }

    public void closeResources() {
        repository.releaseResources();
        logger.info("Repository resources released");
    }

    public static void main(String[] args) {
        logger.info("=== Quantity Measurement Application (UC16) Starting ===");
        QuantityMeasurementApp app = new QuantityMeasurementApp();
        try {
            app.run();
            app.deleteAllMeasurements();
        } finally {
            app.closeResources();
            logger.info("=== Application Shutdown Complete ===");
        }
    }
}
