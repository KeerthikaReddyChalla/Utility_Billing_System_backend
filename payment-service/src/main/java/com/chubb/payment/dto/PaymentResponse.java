package com.chubb.payment.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String paymentId;
    private String billId;
    private double amount;
    private String status;
    private LocalDateTime paymentDate;
}
