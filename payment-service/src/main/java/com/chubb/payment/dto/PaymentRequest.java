package com.chubb.payment.dto;

import lombok.Data;
import lombok.Builder;

@Builder
@Data
public class PaymentRequest {

    private String billId;
    private String consumerId;
    private double amount;

    private String email;
    private String otp;
}
