package com.chubb.notification.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chubb.notification.models.Notification;
import com.chubb.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping("/{consumerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CONSUMER')")
    public List<Notification> getNotifications(@PathVariable("consumerId") String consumerId) {
        return service.getByConsumer(consumerId);
    }
}

