package com.chubb.notification.service;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chubb.notification.dto.NotificationEventDTO;
import com.chubb.notification.models.Notification;
import com.chubb.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public void save(NotificationEventDTO dto) {
        Notification notification = Notification.builder()
                .consumerId(dto.getConsumerId())
                .type(dto.getType())
                .message(dto.getMessage())
                .createdAt(LocalDateTime.now())
                .build();
        repository.save(notification);
    }

    public List<Notification> getByConsumer(String consumerId) {
        return repository.findByConsumerId(consumerId);
    }
}

