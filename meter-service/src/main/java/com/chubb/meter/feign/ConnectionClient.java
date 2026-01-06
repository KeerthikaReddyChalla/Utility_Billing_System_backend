package com.chubb.meter.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@FeignClient(name = "consumer-service")
public interface ConnectionClient {

    @GetMapping("/connections/internal/{connectionId}")
    ConnectionDTO getConnection(@PathVariable("connectionId") String connectionId);
}
