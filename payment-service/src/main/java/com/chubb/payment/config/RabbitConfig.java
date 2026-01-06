package com.chubb.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "payment.exchange";
    public static final String QUEUE = "payment.otp.queue";
    public static final String ROUTING_KEY = "payment.otp";

    @Bean
    public TopicExchange PaymentExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue PaymentOtpQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(PaymentOtpQueue())
                .to(PaymentExchange())
                .with(ROUTING_KEY);
    }


    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
