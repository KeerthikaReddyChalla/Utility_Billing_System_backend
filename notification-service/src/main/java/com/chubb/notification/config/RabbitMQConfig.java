package com.chubb.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /* ===================== EXCHANGES ===================== */

    @Bean
    public TopicExchange utilityExchange() {
        return new TopicExchange("utility.events.exchange");
    }

    @Bean
    public TopicExchange paymentOtpExchange() {
        return new TopicExchange("payment.exchange");
    }

    @Bean
    public TopicExchange billingExchange() {
        return new TopicExchange("billing.exchange");
    }

    // 🔹 NEW: Notification exchange (forgot password, emails, etc.)
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange("notification.exchange");
    }

    /* ===================== QUEUES ===================== */

    @Bean
    public Queue authQueue() {
        return new Queue("notification.auth.queue", true);
    }

    @Bean
    public Queue billQueue() {
        return new Queue("notification.bill.queue", true);
    }

    @Bean
    public Queue paymentQueue() {
        return new Queue("notification.payment.queue", true);
    }

    @Bean
    public Queue paymentOtpQueue() {
        return new Queue("payment.otp.queue", true);
    }

    @Bean
    public Queue overdueReminderQueue() {
        return new Queue("billing.overdue.reminder.queue", true);
    }

    // 🔹 NEW: Forgot password queue
    @Bean
    public Queue forgotPasswordQueue() {
        return new Queue("forgot.password.queue", true);
    }

    /* ===================== BINDINGS ===================== */

    @Bean
    public Binding authBinding() {
        return BindingBuilder
                .bind(authQueue())
                .to(utilityExchange())
                .with("auth.consumer.*");
    }

    @Bean
    public Binding billBinding() {
        return BindingBuilder
                .bind(billQueue())
                .to(utilityExchange())
                .with("bill.*");
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder
                .bind(paymentQueue())
                .to(utilityExchange())
                .with("payment.*");
    }

    @Bean
    public Binding paymentOtpBinding() {
        return BindingBuilder
                .bind(paymentOtpQueue())
                .to(paymentOtpExchange())
                .with("payment.otp");
    }

    @Bean
    public Binding overdueReminderBinding() {
        return BindingBuilder
                .bind(overdueReminderQueue())
                .to(billingExchange())
                .with("billing.overdue.reminder");
    }

    // 🔹 NEW: Forgot password binding
    @Bean
    public Binding forgotPasswordBinding() {
        return BindingBuilder
                .bind(forgotPasswordQueue())
                .to(notificationExchange())
                .with("auth.forgot.password");
    }

    /* ===================== MESSAGE CONVERTER ===================== */

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /* ===================== RABBIT TEMPLATE ===================== */

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
