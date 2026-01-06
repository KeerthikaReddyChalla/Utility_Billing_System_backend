package com.chubb.consumer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chubb.consumer.dto.UtilityMiniDTO;

@FeignClient(name = "utility-service")
public interface UtilityClient {

    @GetMapping("/utilities/internal/{id}")
    void getUtilityById(@PathVariable("id") String id);
    
    @GetMapping("/utilities/internal/{id}")
    UtilityMiniDTO fetchUtility(@PathVariable("id") String id);
}
