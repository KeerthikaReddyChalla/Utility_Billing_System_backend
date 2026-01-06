package com.chubb.notification.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.chubb.notification.events.OtpEmailEvent;
import com.chubb.notification.service.EmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentOtpListener {

    private final EmailService emailService;

    @RabbitListener(queues = "payment.otp.queue")
    public void handleOtp(OtpEmailEvent event) {
        emailService.sendOtpMail(event.getEmail(), event.getOtp());
    }
}
