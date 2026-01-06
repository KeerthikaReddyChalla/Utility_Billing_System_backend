package com.chubb.billing.dto;

import com.chubb.billing.models.TariffType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ConnectionBillingViewDTO {

    private String connectionId;
    private String consumerId;
    private String consumerName;
    private String utilityId;
    private String utilityName;
    private TariffType tariffType;
    private String status;
}
