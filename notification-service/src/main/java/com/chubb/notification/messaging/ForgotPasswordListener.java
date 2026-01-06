package com.chubb.notification.messaging;

import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.chubb.notification.events.ForgotPasswordEvent;
import com.chubb.notification.service.EmailService;

@Component
public class ForgotPasswordListener {

	private final EmailService emailService;
	
	public ForgotPasswordListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "forgot.password.queue")
    public void handleForgotPassword(ForgotPasswordEvent event) {


 
        emailService.sendResetLink(event.getEmail(), event.getToken());
    }
}
