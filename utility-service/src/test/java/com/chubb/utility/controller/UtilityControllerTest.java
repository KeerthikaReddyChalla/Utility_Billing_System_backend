package com.chubb.utility.controller;

import com.chubb.utility.dto.*;
import com.chubb.utility.service.TariffService;
import com.chubb.utility.service.UtilityService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UtilityController.class)
@AutoConfigureMockMvc(addFilters = false) 
class UtilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UtilityService utilityService;

    @MockBean
    private TariffService tariffService;

    @Autowired
    private ObjectMapper objectMapper;

    private UtilityResponse utilityResponse() {
        return UtilityResponse.builder()
                .id("u1")
                .name("Electricity")
                .build();
    }

    private UtilityRequest utilityRequest() {
        return UtilityRequest.builder()
                .name("Electricity")
                .build();
    }

    @Test
    void create_success() throws Exception {
        Mockito.when(utilityService.create(any()))
                .thenReturn(utilityResponse());

        mockMvc.perform(post("/utilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(utilityRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    void getAll_success() throws Exception {
        Mockito.when(utilityService.getAll())
                .thenReturn(List.of(utilityResponse()));

        mockMvc.perform(get("/utilities"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        Mockito.when(utilityService.getById("u1"))
                .thenReturn(utilityResponse());

        mockMvc.perform(get("/utilities/u1"))
                .andExpect(status().isOk());
    }

    @Test
    void update_success() throws Exception {
        Mockito.when(utilityService.update(Mockito.eq("u1"), any()))
                .thenReturn(utilityResponse());

        mockMvc.perform(put("/utilities/u1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(utilityRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(delete("/utilities/u1"))
                .andExpect(status().isNoContent());

        verify(utilityService).delete("u1");
    }

    @Test
    void getInternal_success() throws Exception {
        Mockito.when(utilityService.getById("u1"))
                .thenReturn(utilityResponse());

        mockMvc.perform(get("/utilities/internal/u1"))
                .andExpect(status().isOk());
    }

    @Test
    void fetchUtility_success() throws Exception {
        Mockito.when(utilityService.getById("u1"))
                .thenReturn(utilityResponse());

        mockMvc.perform(get("/utilities/bills/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("u1"))
                .andExpect(jsonPath("$.name").value("Electricity"));
    }
}
