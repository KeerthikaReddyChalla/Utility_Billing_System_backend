package com.chubb.consumer.controller;

import com.chubb.consumer.dto.*;
import com.chubb.consumer.models.RequestStatus;
import com.chubb.consumer.models.TariffType;
import com.chubb.consumer.service.ConnectionService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConnectionController.class)
@AutoConfigureMockMvc(addFilters = false) 
class ConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConnectionService service;

    @Autowired
    private ObjectMapper objectMapper;

    private ConnectionResponseDTO response() {
        return ConnectionResponseDTO.builder()
                .id("conn1")
                .consumerId("c1")
                .build();
    }


    @Test
    void create_success() throws Exception {

    	ConnectionRequestDTO dto =
    	        new ConnectionRequestDTO(
    	                "c1",
    	                "u1",
    	                TariffType.RESIDENTIAL_FLAT
    	        );


        Mockito.when(service.create(Mockito.any()))
                .thenReturn(response());

        mockMvc.perform(post("/connections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getByConsumer_success() throws Exception {

        Mockito.when(service.getByConsumerId("c1"))
                .thenReturn(List.of(response()));

        mockMvc.perform(get("/connections/c1"))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatus_success() throws Exception {

        Mockito.when(service.updateStatus(Mockito.eq("conn1"), Mockito.any()))
                .thenReturn(response());

        mockMvc.perform(put("/connections/conn1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ConnectionUpdateDTO())))
                .andExpect(status().isOk());
    }

    @Test
    void getInternal_success() throws Exception {

        Mockito.when(service.getById("conn1"))
                .thenReturn(response());

        mockMvc.perform(get("/connections/internal/conn1"))
                .andExpect(status().isOk());
    }

    @Test
    void requestConnection_success() throws Exception {

    	ConnectionRequestDTO dto =
    	        new ConnectionRequestDTO(
    	                "c1",
    	                "u1",
    	                TariffType.RESIDENTIAL_FLAT
    	        );
    	
    	mockMvc.perform(post("/connections/request")
    	        .contentType(MediaType.APPLICATION_JSON)
    	        .content(objectMapper.writeValueAsString(dto)))
    	        .andExpect(status().isCreated());

    }

    @Test
    void getPendingRequests_success() throws Exception {

        Mockito.when(service.getPendingRequests())
                .thenReturn(List.of());

        mockMvc.perform(get("/connections/requests/pending"))
                .andExpect(status().isOk());
    }

    @Test
    void approveOrRejectRequest_success() throws Exception {

        ConnectionRequestUpdateDTO dto =
                new ConnectionRequestUpdateDTO(RequestStatus.APPROVED);

        mockMvc.perform(put("/connections/requests/r1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }


    @Test
    void getForBillingOfficer_success() throws Exception {

        Mockito.when(service.getAllForBillingOfficer())
                .thenReturn(List.of());

        mockMvc.perform(get("/connections/internal/billing-view"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllConnectionsInternal_success() throws Exception {

        Mockito.when(service.getAllConnections())
                .thenReturn(List.of());

        mockMvc.perform(get("/connections/internal/all"))
                .andExpect(status().isOk());
    }
}
