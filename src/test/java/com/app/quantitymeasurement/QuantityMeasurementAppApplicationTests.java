package com.app.quantitymeasurement;

import com.app.quantitymeasurement.controller.QuantityInputDTO;
import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * QuantityMeasurementAppApplicationTests — Full Spring Boot integration tests.
 * Starts the application on a random port and uses TestRestTemplate for real HTTP calls.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuantityMeasurementAppApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private QuantityMeasurementRepository repository;


    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/quantities";
        repository.deleteAll();
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private QuantityInputDTO buildInput(double v1, String u1, String t1,
                                        double v2, String u2, String t2) {
        return new QuantityInputDTO(new QuantityDTO(v1, u1, t1), new QuantityDTO(v2, u2, t2));
    }

    private ResponseEntity<QuantityMeasurementDTO> post(String path, QuantityInputDTO body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<QuantityInputDTO> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(baseUrl + path, HttpMethod.POST, entity, QuantityMeasurementDTO.class);
    }

    // ----------------------------------------------------------------
    // Spring Boot Smoke Test
    // ----------------------------------------------------------------

    @Test
    void contextLoads() {
        assertThat(restTemplate).isNotNull();
        assertThat(repository).isNotNull();
    }

    // ----------------------------------------------------------------
    // Compare Tests
    // ----------------------------------------------------------------

    @Test
    void testCompare_EqualQuantities_ReturnsTrue() {
        QuantityInputDTO input = buildInput(1.0, "FEET", "LengthUnit", 12.0, "INCH", "LengthUnit");
        ResponseEntity<QuantityMeasurementDTO> response = post("/compare", input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        QuantityMeasurementDTO body = java.util.Objects.requireNonNull(response.getBody());
        assertThat(body.getResultString()).isEqualTo("true");
        assertThat(body.isError()).isFalse();
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void testCompare_NotEqualQuantities_ReturnsFalse() {
        QuantityInputDTO input = buildInput(1.0, "FEET", "LengthUnit", 10.0, "INCH", "LengthUnit");
        ResponseEntity<QuantityMeasurementDTO> response = post("/compare", input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(java.util.Objects.requireNonNull(response.getBody()).getResultString()).isEqualTo("false");
    }

    @Test
    void testCompare_DifferentTypes_Returns400() {
        QuantityInputDTO input = buildInput(1.0, "FEET", "LengthUnit", 1.0, "KILOGRAM", "WeightUnit");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<QuantityInputDTO> entity = new HttpEntity<>(input, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/compare", HttpMethod.POST, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ----------------------------------------------------------------
    // Convert Tests
    // ----------------------------------------------------------------

    @Test
    void testConvert_FeetToInches_Returns12() {
        QuantityInputDTO input = buildInput(1.0, "FEET", "LengthUnit", 0.0, "INCH", "LengthUnit");
        ResponseEntity<QuantityMeasurementDTO> response = post("/convert", input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        QuantityMeasurementDTO body = java.util.Objects.requireNonNull(response.getBody());
        assertThat(body.getResultValue()).isEqualTo(12.0);
        assertThat(body.isError()).isFalse();
    }

    @Test
    void testConvert_KilogramToGram_Returns1000() {
        QuantityInputDTO input = buildInput(1.0, "KILOGRAM", "WeightUnit", 0.0, "GRAM", "WeightUnit");
        ResponseEntity<QuantityMeasurementDTO> response = post("/convert", input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(java.util.Objects.requireNonNull(response.getBody()).getResultValue()).isEqualTo(1000.0);
    }

    // ----------------------------------------------------------------
    // Add Tests
    // ----------------------------------------------------------------

    @Test
    void testAdd_FeetAndInches_Returns2Feet() {
        QuantityInputDTO input = buildInput(1.0, "FEET", "LengthUnit", 12.0, "INCH", "LengthUnit");
        ResponseEntity<QuantityMeasurementDTO> response = post("/add", input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        QuantityMeasurementDTO body = java.util.Objects.requireNonNull(response.getBody());
        assertThat(body.getResultValue()).isEqualTo(2.0);
        assertThat(body.getResultUnit()).isEqualTo("FEET");
    }

    @Test
    void testAdd_DifferentTypes_Returns400() {
        QuantityInputDTO input = buildInput(1.0, "FEET", "LengthUnit", 1.0, "KILOGRAM", "WeightUnit");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<QuantityInputDTO> entity = new HttpEntity<>(input, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Cannot perform arithmetic");
    }

    // ----------------------------------------------------------------
    // Subtract Tests
    // ----------------------------------------------------------------

    @Test
    void testSubtract_2Feet_Minus1Foot_Returns1Foot() {
        QuantityInputDTO input = buildInput(2.0, "FEET", "LengthUnit", 1.0, "FEET", "LengthUnit");
        ResponseEntity<QuantityMeasurementDTO> response = post("/subtract", input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(java.util.Objects.requireNonNull(response.getBody()).getResultValue()).isEqualTo(1.0);
    }

    // ----------------------------------------------------------------
    // Divide Tests
    // ----------------------------------------------------------------

    @Test
    void testDivide_2Feet_By1Foot_Returns2() {
        QuantityInputDTO input = buildInput(2.0, "FEET", "LengthUnit", 1.0, "FEET", "LengthUnit");
        ResponseEntity<QuantityMeasurementDTO> response = post("/divide", input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(java.util.Objects.requireNonNull(response.getBody()).getResultValue()).isEqualTo(2.0);
    }

    @Test
    void testDivide_ByZero_Returns400() {
        QuantityInputDTO input = buildInput(1.0, "FEET", "LengthUnit", 0.0, "INCH", "LengthUnit");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<QuantityInputDTO> entity = new HttpEntity<>(input, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/divide", HttpMethod.POST, entity, String.class);

        // ArithmeticException → QuantityMeasurementException → 400 BAD_REQUEST
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ----------------------------------------------------------------
    // History Tests
    // ----------------------------------------------------------------

    @Test
    void testGetHistoryByOperation_AfterCompare_ReturnsRecord() {
        post("/compare", buildInput(1.0, "FEET", "LengthUnit", 12.0, "INCH", "LengthUnit"));

        ResponseEntity<List<QuantityMeasurementDTO>> response = restTemplate.exchange(
                baseUrl + "/history/operation/compare",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<QuantityMeasurementDTO> body = java.util.Objects.requireNonNull(response.getBody());
        assertThat(body).hasSize(1);
        assertThat(body.get(0).getOperation()).isEqualTo("compare");
    }

    @Test
    void testGetHistoryByType_LengthUnit_ReturnsAllLengthRecords() {
        post("/compare", buildInput(1.0, "FEET", "LengthUnit", 12.0, "INCH", "LengthUnit"));
        post("/add", buildInput(1.0, "FEET", "LengthUnit", 12.0, "INCH", "LengthUnit"));

        ResponseEntity<List<QuantityMeasurementDTO>> response = restTemplate.exchange(
                baseUrl + "/history/type/LengthUnit",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testGetOperationCount_AfterMultipleCompares() {
        post("/compare", buildInput(1.0, "FEET", "LengthUnit", 12.0, "INCH", "LengthUnit"));
        post("/compare", buildInput(2.0, "FEET", "LengthUnit", 24.0, "INCH", "LengthUnit"));

        ResponseEntity<Long> response = restTemplate.exchange(
                baseUrl + "/count/compare",
                HttpMethod.GET, null, Long.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(2L);
    }

    @Test
    void testGetErrorHistory_AfterErrorOperation_ReturnsError() {
        // This triggers an error (different types)
        QuantityInputDTO input = buildInput(1.0, "FEET", "LengthUnit", 1.0, "KILOGRAM", "WeightUnit");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, new HttpEntity<>(input, headers), String.class);

        ResponseEntity<List<QuantityMeasurementDTO>> response = restTemplate.exchange(
                baseUrl + "/history/errored",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<QuantityMeasurementDTO> body = java.util.Objects.requireNonNull(response.getBody());
        assertThat(body).isNotEmpty();
        assertThat(body.get(0).isError()).isTrue();
    }

    // ----------------------------------------------------------------
    // Actuator & Swagger
    // ----------------------------------------------------------------

    @Test
    void testActuatorHealth_ReturnsUp() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("UP");
    }

    @Test
    void testSwaggerUI_Loads() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/swagger-ui/index.html", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
