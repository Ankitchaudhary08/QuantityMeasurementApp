package com.app.quantitymeasurement.integrationTests;

import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.service.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.util.ConnectionPool;
import org.junit.*;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests verifying the full stack from Controller → Service →
 * Repository → H2 Database.
 */
public class QuantityMeasurementIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(QuantityMeasurementIntegrationTest.class);

    @Rule
    public TestName testName = new TestName();

    private QuantityMeasurementDatabaseRepository repository;
    private QuantityMeasurementServiceImpl service;
    private QuantityMeasurementController controller;

    @Before
    public void setUp() {
        log.info("▶  RUNNING : {}", testName.getMethodName());
        System.setProperty("db.url", "jdbc:h2:mem:testdb_integration;DB_CLOSE_DELAY=-1;MODE=MySQL");
        System.setProperty("db.username", "sa");
        System.setProperty("db.password", "");
        System.setProperty("db.pool.size", "3");
        ConnectionPool.reset();

        repository = new QuantityMeasurementDatabaseRepository();
        service = new QuantityMeasurementServiceImpl(repository);
        controller = new QuantityMeasurementController(service);

        repository.deleteAll();
    }

    @After
    public void tearDown() {
        repository.deleteAll();
        repository.releaseResources();
        ConnectionPool.reset();
        System.clearProperty("db.url");
        System.clearProperty("db.username");
        System.clearProperty("db.password");
        System.clearProperty("db.pool.size");
        log.info("✔  PASSED  : {}", testName.getMethodName());
    }

    // ----------------------------------------------------------------
    // LENGTH
    // ----------------------------------------------------------------

    @Test
    public void testIntegration_CompareLength_FeetVsInch_Equal() {
        QuantityMeasurementEntity result = service.compare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH));

        assertFalse(result.hasError());
        assertTrue(result.getComparisonResult());
        assertEquals(1, repository.getTotalCount());
    }

    @Test
    public void testIntegration_ConvertLength_FeetToInch() {
        QuantityMeasurementEntity result = service.convert(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCH));

        assertFalse(result.hasError());
        assertNotNull(result.getResult());
        assertEquals(12.0, result.getResult().getValue(), 1e-6);
        assertEquals(1, repository.getTotalCount());
    }

    @Test
    public void testIntegration_AddLength_FeetPlusInch() {
        QuantityMeasurementEntity result = service.add(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH));

        assertFalse(result.hasError());
        assertEquals(1, repository.getTotalCount());
    }

    // ----------------------------------------------------------------
    // WEIGHT
    // ----------------------------------------------------------------

    @Test
    public void testIntegration_CompareWeight_KgVsGram_Equal() {
        QuantityMeasurementEntity result = service.compare(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM));

        assertFalse(result.hasError());
        assertTrue(result.getComparisonResult());
    }

    @Test
    public void testIntegration_DivideWeight() {
        QuantityMeasurementEntity result = service.divide(
                new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(5.0, QuantityDTO.WeightUnit.KILOGRAM));

        assertFalse(result.hasError());
        assertTrue(result.hasScalarResult());
        assertEquals(2.0, result.getScalarResult(), 1e-6);
    }

    // ----------------------------------------------------------------
    // TEMPERATURE
    // ----------------------------------------------------------------

    @Test
    public void testIntegration_CompareTemperature_CelsiusVsFahrenheit_Equal() {
        QuantityMeasurementEntity result = service.compare(
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(32.0, QuantityDTO.TemperatureUnit.FAHRENHEIT));

        assertFalse(result.hasError());
        assertTrue(result.getComparisonResult());
    }

    @Test
    public void testIntegration_ConvertTemperature_CelsiusToFahrenheit() {
        QuantityMeasurementEntity result = service.convert(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.FAHRENHEIT));

        assertFalse(result.hasError());
        assertEquals(212.0, result.getResult().getValue(), 1e-6);
    }

    // ----------------------------------------------------------------
    // Cross-Category prevention
    // ----------------------------------------------------------------

    @Test
    public void testIntegration_CrossCategory_Compare_ReturnsError() {
        QuantityMeasurementEntity result = service.compare(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(100.0, QuantityDTO.LengthUnit.FEET));

        assertTrue(result.hasError());
        assertEquals(1, repository.getTotalCount());
    }

    // ----------------------------------------------------------------
    // Query / Filter
    // ----------------------------------------------------------------

    @Test
    public void testIntegration_FilterByOperation() {
        service.compare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH));
        service.convert(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCH));

        List<QuantityMeasurementEntity> compareOps = repository.getMeasurementsByOperation("COMPARE");
        assertEquals(1, compareOps.size());
    }

    @Test
    public void testIntegration_FilterByType() {
        service.compare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH));
        service.compare(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM));

        List<QuantityMeasurementEntity> weightMeasurements = repository.getMeasurementsByType("WEIGHT");
        assertEquals(1, weightMeasurements.size());
    }

    // ----------------------------------------------------------------
    // Connection Pool
    // ----------------------------------------------------------------

    @Test
    public void testIntegration_PoolStatistics() {
        String stats = repository.getPoolStatistics();
        assertNotNull(stats);
        assertTrue("Pool stats should contain 'Pool['", stats.contains("Pool["));
    }

    // ----------------------------------------------------------------
    // Multiple operations accumulate
    // ----------------------------------------------------------------

    @Test
    public void testIntegration_MultipleOperations_AllPersisted() {
        service.compare(new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH));
        service.add(new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(500.0, QuantityDTO.WeightUnit.GRAM));
        service.divide(new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(2.0, QuantityDTO.WeightUnit.KILOGRAM));

        assertEquals(3, repository.getTotalCount());
    }

    // ----------------------------------------------------------------
    // Delete All
    // ----------------------------------------------------------------

    @Test
    public void testIntegration_DeleteAll_EmptiesDatabase() {
        service.compare(new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH));
        assertEquals(1, repository.getTotalCount());

        repository.deleteAll();
        assertEquals(0, repository.getTotalCount());
    }

    // ----------------------------------------------------------------
    // Cache Repository with service
    // ----------------------------------------------------------------

    @Test
    public void testIntegration_CacheRepository_WorksWithService() {
        com.app.quantitymeasurement.repository.QuantityMeasurementCacheRepository cacheRepository = com.app.quantitymeasurement.repository.QuantityMeasurementCacheRepository
                .getInstance();
        cacheRepository.deleteAll();

        QuantityMeasurementServiceImpl cacheService = new QuantityMeasurementServiceImpl(cacheRepository);
        cacheService.compare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH));

        assertEquals(1, cacheRepository.getTotalCount());
        cacheRepository.deleteAll();
    }
}
