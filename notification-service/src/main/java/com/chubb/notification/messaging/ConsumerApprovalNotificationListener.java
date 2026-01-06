package com.chubb.notification.messaging;

import com.chubb.notification.events.ConsumerApprovedEvent;
import com.chubb.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsumerApprovalNotificationListener {

    private final EmailService emailService;

    @RabbitListener(queues = "notification.auth.queue")
    public void handleApprovalEvent(ConsumerApprovedEvent event) {

        if (event.isApproved()) {
            emailService.sendEmail(
                    event.getEmail(),
                    "Consumer Account Approved",
                    "Hello " + event.getName() +
                            ",\n\nYour consumer account has been approved.\n\nYou can now log in and use the system."
            );
        } else {
            emailService.sendEmail(
                    event.getEmail(),
                    "Consumer Account Rejected",
                    "Hello " + event.getName() +
                            ",\n\nUnfortunately, your consumer account has been rejected.\n\nPlease contact support."
            );
        }
    }
}
