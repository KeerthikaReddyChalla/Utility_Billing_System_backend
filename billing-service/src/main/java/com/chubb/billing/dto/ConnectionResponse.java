package com.chubb.billing.dto;

import lombok.*;
import com.chubb.billing.models.TariffType;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ConnectionResponse {

    private String id;
    private String consumerId;
    private String utilityId;
    private TariffType tariffType;
    private String status;
}
