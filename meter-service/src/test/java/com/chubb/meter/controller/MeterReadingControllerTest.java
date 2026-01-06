package com.chubb.meter.controller;

import com.chubb.meter.dto.MeterReadingRequest;
import com.chubb.meter.dto.MeterReadingResponse;
import com.chubb.meter.repository.MeterReadingRepository;
import com.chubb.meter.service.MeterReadingService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeterReadingController.class)
@AutoConfigureMockMvc(addFilters = false)
class MeterReadingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeterReadingService service;

    @MockBean
    private MeterReadingRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private MeterReadingResponse response() {
        return MeterReadingResponse.builder()
                .connectionId("conn1")
                .consumerId("c1")
                .utilityId("u1")
                .readingValue(150)
                .readingDate(LocalDate.now())
                .build();
    }

    @Test
    void create_success() throws Exception {

        MeterReadingRequest request = MeterReadingRequest.builder()
                .connectionId("c1")
                .consumerId("cons1")
                .utilityId("u1")
                .readingValue(123.0)
                .readingDate(LocalDate.now())
                .build();

        MeterReadingResponse response = MeterReadingResponse.builder()
                .connectionId("c1")
                .readingValue(123.0)
                .build();

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/meter-readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }


    @Test
    void getByConnection_success() throws Exception {

        Mockito.when(service.getByConnection("conn1"))
                .thenReturn(List.of(response()));

        mockMvc.perform(get("/meter-readings/conn1"))
                .andExpect(status().isOk());
    }

    @Test
    void getLatest_success() throws Exception {

        Mockito.when(service.getLatest("conn1"))
                .thenReturn(response());

        mockMvc.perform(get("/meter-readings/latest/conn1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPrevious_found() throws Exception {

        Mockito.when(service.getPrevious("conn1"))
                .thenReturn(response());

        mockMvc.perform(get("/meter-readings/previous/conn1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPrevious_notFound_returns204() throws Exception {

        Mockito.when(service.getPrevious("conn1"))
                .thenReturn(null);

        mockMvc.perform(get("/meter-readings/previous/conn1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void hasReading_success() throws Exception {

        Mockito.when(repository.existsByConnectionId("conn1"))
                .thenReturn(true);

        mockMvc.perform(get("/meter-readings/internal/exists/conn1"))
                .andExpect(status().isOk());
    }

    @Test
    void getConnectionsWithReadings_success() throws Exception {

        Mockito.when(service.getAllConnectionIdsWithReadings())
                .thenReturn(List.of("conn1", "conn2"));

        mockMvc.perform(get("/meter-readings/internal/connections-with-readings"))
                .andExpect(status().isOk());
    }
}
