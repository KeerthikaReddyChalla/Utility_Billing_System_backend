package com.chubb.billing.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chubb.billing.dto.ConnectionResponse;
import com.chubb.billing.dto.ConsumerResponse;

@FeignClient(
	    name = "consumer-service",
	    contextId = "consumerClient"
	)
public interface ConsumerClient {

    @GetMapping("/consumers/{consumerId}")
    ConsumerResponse getConsumer(@PathVariable String consumerId);
    
    @GetMapping("/connections/internal/{connectionId}")
    ConnectionResponse getConnection(@PathVariable String connectionId);
    
    @GetMapping("/connections/internal/all")
    List<ConnectionResponse> getAllConnections();
}
