package com.chubb.billing.dto;

import lombok.*;
import com.chubb.billing.models.BillStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BillResponse {

    private String billId;

    private String connectionId;
    private String consumerId;

    private String consumerName;
    private String consumerEmail;
    private String utilityName;

    private double unitsConsumed;
    private double amount;
    private BillStatus status;
    private LocalDate billingCycle;
}
