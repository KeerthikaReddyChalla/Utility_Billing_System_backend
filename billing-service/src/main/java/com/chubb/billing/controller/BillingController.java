package com.chubb.billing.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.chubb.billing.client.ConnectionClient;
import com.chubb.billing.dto.BillResponse;
import com.chubb.billing.dto.ConnectionBillingViewDTO;
import com.chubb.billing.dto.GenerateBillRequest;
import com.chubb.billing.models.Bill;
import com.chubb.billing.service.BillingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService service;
    private final ConnectionClient connectionClient;

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('BILLING_OFFICER')")
    public BillResponse generateBill(@Valid @RequestBody GenerateBillRequest req) {
    	
        return service.generateBill(req);
    }

    @GetMapping("/consumer/{consumerId}")
    @PreAuthorize("hasAnyRole('ADMIN','CONSUMER')")
    public List<Bill> getByConsumer(@PathVariable("consumerId") String consumerId) {
        return service.getBillsByConsumer(consumerId);
    }
    
    @PutMapping("/{billId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ACCOUNTS_OFFICER','CONSUMER')")
    public void markBillAsPaid(@PathVariable("billId") String billId) {
        service.markBillAsPaid(billId);
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('BILLING_OFFICER', 'ACCOUNTS_OFFICER','ADMIN')")
    public List<Bill> getAllBills() {
        return service.getAllBills();
    }
    
    @GetMapping("/connections")
    @PreAuthorize("hasRole('BILLING_OFFICER')")
    public List<ConnectionBillingViewDTO> getConnectionsForBilling() {
        return connectionClient.getConnectionsForBilling();
    }
    
    @GetMapping("/connections/without-readings")
    @PreAuthorize("hasRole('BILLING_OFFICER')")
    public List<ConnectionBillingViewDTO> getConnectionsWithoutReadings() {
        return service.getConnectionsWithoutReadings();
    }
    
    @PostMapping("/{billId}/send-reminder")
    @PreAuthorize("hasRole('ACCOUNTS_OFFICER')")
    public void sendReminder(@PathVariable("billId") String billId) {
        service.sendOverdueReminder(billId);
    }



}
