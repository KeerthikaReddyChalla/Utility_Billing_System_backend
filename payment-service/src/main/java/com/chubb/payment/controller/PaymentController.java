package com.chubb.payment.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.chubb.payment.dto.PaymentRequest;
import com.chubb.payment.dto.PaymentResponse;
import com.chubb.payment.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CONSUMER')")
    public PaymentResponse get(@PathVariable("id") String id) {
        return service.getById(id);
    }

    @GetMapping("/bill/{billId}")
    @PreAuthorize("hasAnyRole('ADMIN','CONSUMER')")
    public List<PaymentResponse> byBill(@PathVariable("billId") String billId) {
        return service.getByBill(billId);
    }

    @GetMapping("/consumer/{consumerId}")
    @PreAuthorize("hasAnyRole('ADMIN','CONSUMER')")
    public List<PaymentResponse> byConsumer(@PathVariable("consumerId") String consumerId) {
        return service.getByConsumer(consumerId);
    }
    
    
    @GetMapping
    @PreAuthorize("hasRole('ACCOUNTS_OFFICER')")
    public List<PaymentResponse> getAllPayments() {
        return service.getAllPayments();
    }

    @PostMapping("/send-otp")
    @PreAuthorize("hasRole('CONSUMER')")
    public void sendOtp(@RequestParam("email") String email) {
        service.sendOtp(email);
    }


    @PostMapping("/verify-otp")
    @PreAuthorize("hasRole('CONSUMER')")
    public PaymentResponse verifyOtpAndPay(
            @Valid @RequestBody PaymentRequest req) {
        return service.verifyOtpAndCompletePayment(req);
    }

}
