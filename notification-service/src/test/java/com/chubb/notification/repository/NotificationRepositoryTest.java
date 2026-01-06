package com.chubb.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.chubb.notification.models.Notification;

@DataMongoTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void findByConsumerId_success() {

        repository.save(
                Notification.builder()
                        .consumerId("c1")
                        .type("PAYMENT")
                        .message("Paid")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        List<Notification> result = repository.findByConsumerId("c1");

        assertThat(result).hasSize(1);
    }
}
