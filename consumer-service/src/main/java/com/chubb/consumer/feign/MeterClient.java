package com.chubb.consumer.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "meter-service")
public interface MeterClient {
	@GetMapping("/meter-readings/internal/exists/{connectionId}")
    boolean hasMeterReading(@PathVariable("connectionId") String connectionId);
}
