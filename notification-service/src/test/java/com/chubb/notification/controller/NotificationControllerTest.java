package com.chubb.notification.controller;

import com.chubb.notification.models.Notification;
import com.chubb.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false) 
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification notification() {
        Notification n = new Notification();
        n.setId("n1");
        n.setConsumerId("c1");
        n.setMessage("Bill overdue");
        return n;
    }

    @Test
    void getNotifications_success() throws Exception {

        Mockito.when(service.getByConsumer("c1"))
                .thenReturn(List.of(notification()));

        mockMvc.perform(get("/notifications/c1"))
                .andExpect(status().isOk());
    }
}
