package com.chubb.billing.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "bills")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    private String id;

    private String consumerId;
    private String connectionId;
    private String utilityId;

    private LocalDate billingCycle;
    private double unitsConsumed;
    private double amount;

    private BillStatus status;
    private LocalDateTime generatedAt;
    private String consumerName;

    private String consumerEmail;
    private String utilityName;
}
