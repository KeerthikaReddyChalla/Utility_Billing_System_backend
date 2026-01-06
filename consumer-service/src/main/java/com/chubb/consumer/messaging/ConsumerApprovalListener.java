package com.chubb.consumer.messaging;

import com.chubb.consumer.config.RabbitConfig;
import com.chubb.consumer.dto.ConsumerApprovedEvent;
import com.chubb.consumer.models.Consumer;
import com.chubb.consumer.repository.ConsumerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsumerApprovalListener {

    private final ConsumerRepository consumerRepository;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handleConsumerApproved(ConsumerApprovedEvent event) {

       

        if (!event.isApproved()) {
         
            return;
        }

        if (consumerRepository.existsById(event.getUserId())) {
          
            return;
        }

        Consumer consumer = Consumer.builder()
                .id(event.getUserId())
                .fullName(event.getName())
                .email(event.getEmail())
                .build();

        consumerRepository.save(consumer);

       
    }
}
