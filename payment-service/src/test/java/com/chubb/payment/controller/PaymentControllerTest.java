package com.chubb.payment.controller;

import com.chubb.payment.dto.PaymentRequest;
import com.chubb.payment.dto.PaymentResponse;
import com.chubb.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false) 

class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService service;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentResponse response() {
        return PaymentResponse.builder()
                .paymentId("p1")
                .billId("b1")
                .amount(500.0)
                .status("SUCCESS")
                .paymentDate(LocalDateTime.now())
                .build();
    }

    @Test
    void getById_success() throws Exception {
        when(service.getById("p1")).thenReturn(response());

        mockMvc.perform(get("/payments/p1"))
                .andExpect(status().isOk());
    }

    @Test
    void byBill_success() throws Exception {
        when(service.getByBill("b1"))
                .thenReturn(List.of(response()));

        mockMvc.perform(get("/payments/bill/b1"))
                .andExpect(status().isOk());
    }

    @Test
    void byConsumer_success() throws Exception {
        when(service.getByConsumer("c1"))
                .thenReturn(List.of(response()));

        mockMvc.perform(get("/payments/consumer/c1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllPayments_success() throws Exception {
        when(service.getAllPayments())
                .thenReturn(List.of(response()));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk());
    }

    @Test
    void sendOtp_success() throws Exception {
        mockMvc.perform(post("/payments/send-otp")
                .param("email", "test@mail.com"))
                .andExpect(status().isOk());

        verify(service).sendOtp("test@mail.com");
    }

    
}
