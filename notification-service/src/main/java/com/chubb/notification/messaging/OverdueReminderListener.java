package com.chubb.notification.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chubb.notification.events.OverdueBillReminderEvent;
import com.chubb.notification.service.EmailService;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Component
@RequiredArgsConstructor
public class OverdueReminderListener {

	private final EmailService emailService;


    @RabbitListener(queues = "billing.overdue.reminder.queue")
    public void handleOverdueReminder(OverdueBillReminderEvent event) {

        String subject = "⚠️ Overdue Utility Bill Reminder";

        String body = """
            Dear Customer,

            Your utility bill is overdue.

            Bill ID: %s
            Amount Due: ₹%.2f

            Please make the payment at the earliest to avoid penalties.

            Regards,
            Utility Billing Team
            """.formatted(
                event.getBillId(),
                event.getAmount()
            );

        emailService.send(
            event.getConsumerEmail(),
            subject,
            body
        );
    }
}
