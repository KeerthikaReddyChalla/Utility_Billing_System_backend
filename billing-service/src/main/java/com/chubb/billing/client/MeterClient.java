package com.chubb.billing.client;

import com.chubb.billing.dto.MeterReadingResponse;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "meter-service")
public interface MeterClient {

    @GetMapping("/meter-readings/latest/{connectionId}")
    MeterReadingResponse getLatest(
            @PathVariable("connectionId") String connectionId
    );

    @GetMapping("/meter-readings/previous/{connectionId}")
    MeterReadingResponse getPrevious(
            @PathVariable("connectionId") String connectionId
    );
    
    @GetMapping("/meter-readings/internal/connections-with-readings")
    List<String> getConnectionsWithReadings();
}
