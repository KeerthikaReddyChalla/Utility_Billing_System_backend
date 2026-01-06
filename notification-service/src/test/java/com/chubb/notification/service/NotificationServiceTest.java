package com.chubb.notification.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chubb.notification.dto.NotificationEventDTO;
import com.chubb.notification.models.Notification;
import com.chubb.notification.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService service;

    @Test
    void save_success_maps_all_fields() {

        NotificationEventDTO dto = NotificationEventDTO.builder()
                .consumerId("c1")
                .type("PAYMENT")
                .message("Payment successful")
                .build();

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        service.save(dto);

        verify(repository).save(captor.capture());

        Notification saved = captor.getValue();

        assertThat(saved.getConsumerId()).isEqualTo("c1");
        assertThat(saved.getType()).isEqualTo("PAYMENT");
        assertThat(saved.getMessage()).isEqualTo("Payment successful");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void getByConsumer_success_returns_data() {

        Notification n = Notification.builder()
                .consumerId("c1")
                .type("REMINDER")
                .message("Bill overdue")
                .build();

        when(repository.findByConsumerId("c1"))
                .thenReturn(List.of(n));

        List<Notification> result =
                service.getByConsumer("c1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessage())
                .isEqualTo("Bill overdue");
    }
}
