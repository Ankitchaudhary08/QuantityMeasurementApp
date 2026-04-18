package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import com.app.quantitymeasurement.repository.UserRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * QuantityMeasurementControllerTest — MockMvc-based unit tests for the REST controller.
 * Uses @WebMvcTest (web layer only) with @MockBean service and @WithMockUser for security.
 */
@WebMvcTest(QuantityMeasurementController.class)
@WithMockUser
class QuantityMeasurementControllerTest {

    @MockBean
    private UserRepository userRepository; // required by CustomOAuth2UserService in security context

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IQuantityMeasurementService service;

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private QuantityInputDTO buildInput(String unit1, String type1, double val1,
                                        String unit2, String type2, double val2) {
        QuantityDTO q1 = new QuantityDTO(val1, unit1, type1);
        QuantityDTO q2 = new QuantityDTO(val2, unit2, type2);
        return new QuantityInputDTO(q1, q2);
    }

    private QuantityMeasurementDTO buildDTO(String operation, String resultStr, double resultVal) {
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setOperation(operation);
        dto.setThisValue(1.0);
        dto.setThisUnit("FEET");
        dto.setThisMeasurementType("LengthUnit");
        dto.setThatValue(12.0);
        dto.setThatUnit("INCH");
        dto.setThatMeasurementType("LengthUnit");
        dto.setResultString(resultStr);
        dto.setResultValue(resultVal);
        dto.setError(false);
        return dto;
    }

    // ----------------------------------------------------------------
    // POST /compare
    // ----------------------------------------------------------------

    @Test
    void testCompare_Returns200WithResult() throws Exception {
        QuantityInputDTO input = buildInput("FEET", "LengthUnit", 1.0, "INCH", "LengthUnit", 12.0);
        QuantityMeasurementDTO mockResult = buildDTO("compare", "true", 0.0);

        Mockito.when(service.compare(any(), any())).thenReturn(mockResult);

        mockMvc.perform(post("/api/v1/quantities/compare")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("compare"))
                .andExpect(jsonPath("$.resultString").value("true"))
                .andExpect(jsonPath("$.error").value(false));
    }

    @Test
    void testCompare_InvalidInput_Returns400() throws Exception {
        // Missing required fields — empty JSON object causes validation failure
        mockMvc.perform(post("/api/v1/quantities/compare")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------------------------------------------
    // POST /convert
    // ----------------------------------------------------------------

    @Test
    void testConvert_Returns200WithResult() throws Exception {
        QuantityInputDTO input = buildInput("FEET", "LengthUnit", 1.0, "INCH", "LengthUnit", 0.0);
        QuantityMeasurementDTO mockResult = buildDTO("convert", null, 12.0);

        Mockito.when(service.convert(any(), any())).thenReturn(mockResult);

        mockMvc.perform(post("/api/v1/quantities/convert")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultValue").value(12.0))
                .andExpect(jsonPath("$.operation").value("convert"));
    }

    // ----------------------------------------------------------------
    // POST /add
    // ----------------------------------------------------------------

    @Test
    void testAdd_Returns200WithResult() throws Exception {
        QuantityInputDTO input = buildInput("FEET", "LengthUnit", 1.0, "INCH", "LengthUnit", 12.0);
        QuantityMeasurementDTO mockResult = buildDTO("add", null, 2.0);
        mockResult.setResultUnit("FEET");

        Mockito.when(service.add(any(), any())).thenReturn(mockResult);

        mockMvc.perform(post("/api/v1/quantities/add")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultValue").value(2.0))
                .andExpect(jsonPath("$.resultUnit").value("FEET"));
    }

    // ----------------------------------------------------------------
    // POST /subtract
    // ----------------------------------------------------------------

    @Test
    void testSubtract_Returns200WithResult() throws Exception {
        QuantityInputDTO input = buildInput("FEET", "LengthUnit", 2.0, "FEET", "LengthUnit", 1.0);
        QuantityMeasurementDTO mockResult = buildDTO("subtract", null, 1.0);

        Mockito.when(service.subtract(any(), any())).thenReturn(mockResult);

        mockMvc.perform(post("/api/v1/quantities/subtract")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("subtract"));
    }

    // ----------------------------------------------------------------
    // POST /divide
    // ----------------------------------------------------------------

    @Test
    void testDivide_Returns200WithResult() throws Exception {
        QuantityInputDTO input = buildInput("FEET", "LengthUnit", 2.0, "FEET", "LengthUnit", 1.0);
        QuantityMeasurementDTO mockResult = buildDTO("divide", null, 2.0);

        Mockito.when(service.divide(any(), any())).thenReturn(mockResult);

        mockMvc.perform(post("/api/v1/quantities/divide")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultValue").value(2.0));
    }

    // ----------------------------------------------------------------
    // GET /history/operation/{operation}
    // ----------------------------------------------------------------

    @Test
    void testGetHistoryByOperation_Returns200() throws Exception {
        QuantityMeasurementDTO dto = buildDTO("compare", "true", 0.0);
        Mockito.when(service.getHistoryByOperation("compare")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/quantities/history/operation/compare"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].operation").value("compare"));
    }

    // ----------------------------------------------------------------
    // GET /count/{operation}
    // ----------------------------------------------------------------

    @Test
    void testGetOperationCount_Returns200() throws Exception {
        Mockito.when(service.getOperationCount("compare")).thenReturn(3L);

        mockMvc.perform(get("/api/v1/quantities/count/compare"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    // ----------------------------------------------------------------
    // GET /history/errored
    // ----------------------------------------------------------------

    @Test
    void testGetErrorHistory_Returns200EmptyList() throws Exception {
        Mockito.when(service.getErrorHistory()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/quantities/history/errored"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
