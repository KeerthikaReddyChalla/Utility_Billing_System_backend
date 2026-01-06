package com.chubb.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.chubb.notification.dto.NotificationEventDTO;
import com.chubb.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "notification.bill.queue")
    public void handleBillGenerated(NotificationEventDTO event) {
        notificationService.save(event);
    }

    @RabbitListener(queues = "notification.payment.queue")
    public void handlePaymentEvent(NotificationEventDTO event) {
        notificationService.save(event);
    }

    @RabbitListener(queues = "notification.auth.queue")
    public void handleAuthEvent(NotificationEventDTO event) {
        notificationService.save(event);
    }
}
