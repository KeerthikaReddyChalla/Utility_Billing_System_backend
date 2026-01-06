package com.chubb.payment.models;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    private String id;
    private String billId;
    private String consumerId;
    private double amount;
    private String method; // CARD, UPI
    private PaymentStatus status; // INITIATED, COMPLETED
    private LocalDateTime paidAt;
}
