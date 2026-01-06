package com.chubb.billing.controller;

import com.chubb.billing.client.ConnectionClient;
import com.chubb.billing.dto.BillResponse;
import com.chubb.billing.dto.ConnectionBillingViewDTO;
import com.chubb.billing.dto.GenerateBillRequest;
import com.chubb.billing.models.Bill;
import com.chubb.billing.service.BillingService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BillingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillingService service;

    @MockBean
    private ConnectionClient connectionClient;

    @Autowired
    private ObjectMapper objectMapper;

    private BillResponse mockBillResponse() {
        BillResponse r = new BillResponse();
        r.setBillId("B1");
        r.setConsumerId("C1");
        return r;
    }

    private Bill mockBill() {
        Bill bill = new Bill();
        bill.setId("B1");
        return bill;
    }

    private ConnectionBillingViewDTO mockConnection() {
        ConnectionBillingViewDTO dto = new ConnectionBillingViewDTO();
        dto.setConnectionId("CONN1");
        return dto;
    }

    @Test
    void generateBill_success() throws Exception {

        GenerateBillRequest req = new GenerateBillRequest();
        req.setConnectionId("CONN1");
        req.setBillingCycle(LocalDate.of(2025, 1, 1));

        Mockito.when(service.generateBill(Mockito.any()))
                .thenReturn(mockBillResponse());

        mockMvc.perform(post("/bills/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }


    @Test
    void getByConsumer_success() throws Exception {

        Mockito.when(service.getBillsByConsumer("C1"))
                .thenReturn(List.of(mockBill()));

        mockMvc.perform(get("/bills/consumer/C1"))
                .andExpect(status().isOk());
    }

    @Test
    void markBillAsPaid_success() throws Exception {

        mockMvc.perform(put("/bills/B1/status"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllBills_success() throws Exception {

        Mockito.when(service.getAllBills())
                .thenReturn(List.of(mockBill()));

        mockMvc.perform(get("/bills"))
                .andExpect(status().isOk());
    }

    @Test
    void getConnectionsForBilling_success() throws Exception {

        Mockito.when(connectionClient.getConnectionsForBilling())
                .thenReturn(List.of(mockConnection()));

        mockMvc.perform(get("/bills/connections"))
                .andExpect(status().isOk());
    }

    @Test
    void getConnectionsWithoutReadings_success() throws Exception {

        Mockito.when(service.getConnectionsWithoutReadings())
                .thenReturn(List.of(mockConnection()));

        mockMvc.perform(get("/bills/connections/without-readings"))
                .andExpect(status().isOk());
    }

    @Test
    void sendReminder_success() throws Exception {

        mockMvc.perform(post("/bills/B1/send-reminder"))
                .andExpect(status().isOk());
    }
}
