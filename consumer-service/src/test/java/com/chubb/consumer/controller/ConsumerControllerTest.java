package com.chubb.consumer.controller;

import com.chubb.consumer.dto.ConsumerRequestDTO;
import com.chubb.consumer.dto.ConsumerResponseDTO;
import com.chubb.consumer.service.ConsumerService;
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

@WebMvcTest(ConsumerController.class)
@AutoConfigureMockMvc(addFilters = false) 
class ConsumerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsumerService service;

    @Autowired
    private ObjectMapper objectMapper;


    private ConsumerResponseDTO response() {
        return ConsumerResponseDTO.builder()
                .id("c1")
                .email("test@mail.com")
                .build();
    }


    @Test
    void create_success() throws Exception {

        Mockito.when(service.createConsumer(Mockito.any()))
                .thenReturn(response());

        ConsumerRequestDTO dto = new ConsumerRequestDTO(
                "u1",
                "John",
                "john@mail.com",
                "9999999999",
                "Address"
        );

        mockMvc.perform(post("/consumers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))) 
                .andExpect(status().isCreated());
    }


    @Test
    void getById_success() throws Exception {

        Mockito.when(service.getById("c1"))
                .thenReturn(response());

        mockMvc.perform(get("/consumers/c1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_success() throws Exception {

        Mockito.when(service.getAll())
                .thenReturn(List.of(response()));

        mockMvc.perform(get("/consumers"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_success() throws Exception {

        mockMvc.perform(delete("/consumers/c1"))
                .andExpect(status().isNoContent());
    }
}
