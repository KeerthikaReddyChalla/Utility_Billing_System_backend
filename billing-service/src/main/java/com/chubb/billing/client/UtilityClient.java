package com.chubb.billing.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chubb.billing.dto.UtilityResponse;

@FeignClient(name = "utility-service")
public interface UtilityClient {

    @GetMapping("/utilities/{utilityId}")
    UtilityResponse getUtility(@PathVariable String utilityId);
}
