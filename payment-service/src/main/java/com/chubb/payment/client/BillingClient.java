package com.chubb.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "billing-service")
public interface BillingClient {

    @PutMapping("/bills/{billId}/status")
    void markBillAsPaid(@PathVariable("billId") String billId);
}
