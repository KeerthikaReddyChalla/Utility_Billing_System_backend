package com.chubb.billing.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
	    name = "utility-service",
	    contextId = "tariffClient"
	)
public interface TariffClient {

    @GetMapping("/tariffs/utility/{utilityId}/rate")
    Double getRate(
            @PathVariable("utilityId") String utilityId
    );
}
