package com.chubb.consumer.dto;

import com.chubb.consumer.models.TariffType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConnectionBillingViewDTO {

    private String connectionId;

    private String consumerId;
    private String consumerName;

    private String utilityId;
    private String utilityName;

    private TariffType tariffType;
    private String status;
}
