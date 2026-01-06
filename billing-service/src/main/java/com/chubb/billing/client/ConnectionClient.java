package com.chubb.billing.client;

import com.chubb.billing.dto.ConnectionBillingViewDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "consumer-service")
public interface ConnectionClient {

    @GetMapping("/connections/internal/billing-view")
    List<ConnectionBillingViewDTO> getConnectionsForBilling();
}
