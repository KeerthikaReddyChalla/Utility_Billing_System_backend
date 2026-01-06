package com.chubb.notification.consumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.chubb.notification.dto.NotificationEventDTO;
import com.chubb.notification.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private NotificationService service;

    @InjectMocks
    private NotificationConsumer consumer;

    @Test
    void consumeBillEvent_success() {
        NotificationEventDTO dto =
                new NotificationEventDTO("c1", "BILL", "Bill Generated");

        consumer.handleBillGenerated(dto);

        verify(service, times(1)).save(dto);
    }
}

