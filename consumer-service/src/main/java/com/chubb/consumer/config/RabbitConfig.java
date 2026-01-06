package com.chubb.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // MUST MATCH AUTH + NOTIFICATION
    public static final String EXCHANGE = "utility.events.exchange";
    public static final String QUEUE = "consumer.approved.queue";
    public static final String ROUTING_KEY = "auth.consumer.approved";

    @Bean
    public TopicExchange utilityEventsExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue consumerApprovedQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding consumerApprovedBinding() {
        return BindingBuilder
                .bind(consumerApprovedQueue())
                .to(utilityEventsExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
