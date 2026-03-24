package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.util.ConnectionPool;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for QuantityMeasurementDatabaseRepository using H2 in-memory
 * database.
 */
public class QuantityMeasurementDatabaseRepositoryTest {

    private QuantityMeasurementDatabaseRepository repository;

    @Before
    public void setUp() {
        // Use isolated H2 in-memory DB for each test class
        System.setProperty("db.url", "jdbc:h2:mem:testdb_repo;DB_CLOSE_DELAY=-1;MODE=MySQL");
        System.setProperty("db.username", "sa");
        System.setProperty("db.password", "");
        System.setProperty("db.pool.size", "3");
        ConnectionPool.reset();
        repository = new QuantityMeasurementDatabaseRepository();
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
    }

    // ---- Save & Retrieve ----

    @Test
    public void testSaveAndRetrieveAll() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH),
                "COMPARE", true);

        repository.save(entity);

        List<QuantityMeasurementEntity> all = repository.getAllMeasurements();
        assertEquals(1, all.size());
    }

    @Test
    public void testSaveMultipleEntities() {
        repository.save(new QuantityMeasurementEntity(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM),
                "COMPARE", true));
        repository.save(new QuantityMeasurementEntity(
                new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(5.0, QuantityDTO.WeightUnit.KILOGRAM),
                "DIVIDE"));

        assertEquals(2, repository.getTotalCount());
    }

    // ---- Filter by Operation ----

    @Test
    public void testGetMeasurementsByOperation() {
        repository.save(new QuantityMeasurementEntity(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH),
                "COMPARE", true));
        repository.save(new QuantityMeasurementEntity(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCH),
                "CONVERT"));

        List<QuantityMeasurementEntity> compareOps = repository.getMeasurementsByOperation("COMPARE");
        assertEquals(1, compareOps.size());
    }

    // ---- Filter by Measurement Type ----

    @Test
    public void testGetMeasurementsByType() {
        repository.save(new QuantityMeasurementEntity(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH),
                "COMPARE", true));
        repository.save(new QuantityMeasurementEntity(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM),
                "COMPARE", true));

        List<QuantityMeasurementEntity> lengthMeasurements = repository.getMeasurementsByType("LENGTH");
        assertEquals(1, lengthMeasurements.size());
    }

    // ---- Count ----

    @Test
    public void testGetTotalCount_Empty() {
        assertEquals(0, repository.getTotalCount());
    }

    @Test
    public void testGetTotalCount_AfterSaves() {
        repository.save(new QuantityMeasurementEntity(
                new QuantityDTO(1.0, QuantityDTO.VolumeUnit.LITRE),
                new QuantityDTO(1000.0, QuantityDTO.VolumeUnit.MILLILITRE),
                "COMPARE", true));
        repository.save(new QuantityMeasurementEntity(
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(32.0, QuantityDTO.TemperatureUnit.FAHRENHEIT),
                "COMPARE", true));

        assertEquals(2, repository.getTotalCount());
    }

    // ---- Delete All ----

    @Test
    public void testDeleteAll() {
        repository.save(new QuantityMeasurementEntity(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH),
                "COMPARE", true));
        assertEquals(1, repository.getTotalCount());

        repository.deleteAll();
        assertEquals(0, repository.getTotalCount());
    }

    // ---- Error Entity ----

    @Test
    public void testSaveErrorEntity() {
        repository.save(new QuantityMeasurementEntity("Cannot compare different categories"));
        assertEquals(1, repository.getTotalCount());
        List<QuantityMeasurementEntity> all = repository.getAllMeasurements();
        assertTrue(all.get(0).hasError());
        assertEquals("Cannot compare different categories", all.get(0).getErrorMessage());
    }

    // ---- Pool Statistics ----

    @Test
    public void testGetPoolStatistics() {
        String stats = repository.getPoolStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("Pool["));
    }

    // ---- SQL Injection Prevention ----

    @Test
    public void testSqlInjectionPrevention() {
        // Parameterised queries should treat this as a literal string, returning 0
        // results
        List<QuantityMeasurementEntity> results = repository
                .getMeasurementsByOperation("'; DROP TABLE quantity_measurements; --");
        assertEquals(0, results.size());
        // Table should still exist — saving still works
        repository.save(new QuantityMeasurementEntity(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCH),
                "COMPARE", true));
        assertEquals(1, repository.getTotalCount());
    }
}
