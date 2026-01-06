package com.chubb.utility.controller;

import com.chubb.utility.dto.*;
import com.chubb.utility.models.TariffType;
import com.chubb.utility.service.TariffService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TariffController.class)
@AutoConfigureMockMvc(addFilters = false) 
class TariffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TariffService service;

    @Autowired
    private ObjectMapper objectMapper;

    private TariffResponse response() {
        return TariffResponse.builder()
                .id("t1")
                .utilityId("u1")
                .ratePerUnit(5.0)
                .build();
    }

    @Test
    void create_success() throws Exception {

        CreateTariffRequest req = new CreateTariffRequest(
                "u1",
                TariffType.RESIDENTIAL_FLAT,
                5.0,
                50.0
        );

        TariffResponse response = TariffResponse.builder()
                .id("t1")
                .utilityId("u1")
                .tariffType(TariffType.RESIDENTIAL_FLAT)
                .ratePerUnit(5.0)
                .fixedCharge(50.0)
                .active(true)
                .build();

        when(service.create(any()))
                .thenReturn(response);

        mockMvc.perform(post("/tariffs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }


    @Test
    void update_success() throws Exception {
        Mockito.when(service.update(Mockito.eq("t1"), any()))
                .thenReturn(response());

        UpdateTariffRequest req = UpdateTariffRequest.builder()
                .ratePerUnit(6.0)
                .build();

        mockMvc.perform(put("/tariffs/t1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_success() throws Exception {
        Mockito.when(service.getAll())
                .thenReturn(List.of(response()));

        mockMvc.perform(get("/tariffs"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        Mockito.when(service.getById("t1"))
                .thenReturn(response());

        mockMvc.perform(get("/tariffs/t1"))
                .andExpect(status().isOk());
    }

    @Test
    void getByUtility_success() throws Exception {
        Mockito.when(service.getByUtility("u1"))
                .thenReturn(List.of(response()));

        mockMvc.perform(get("/tariffs/utility/u1"))
                .andExpect(status().isOk());
    }

    @Test
    void getRate_success() throws Exception {
        Mockito.when(service.getRateByUtilityId("u1"))
                .thenReturn(5.0);

        mockMvc.perform(get("/tariffs/utility/u1/rate"))
                .andExpect(status().isOk())
                .andExpect(content().string("5.0"));
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(delete("/tariffs/t1"))
                .andExpect(status().isNoContent());

        verify(service).delete("t1");
    }
}
