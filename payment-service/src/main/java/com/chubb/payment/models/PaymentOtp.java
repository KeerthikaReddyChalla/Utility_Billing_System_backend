package com.chubb.payment.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "payment_otps")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOtp {

    @Id
    private String id;

    private String email;
    private String otp;

    private LocalDateTime expiresAt;
}
