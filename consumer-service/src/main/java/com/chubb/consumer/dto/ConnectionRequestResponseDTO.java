package com.chubb.consumer.dto;

import com.chubb.consumer.models.TariffType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConnectionRequestResponseDTO {

    private String id;
    private String consumerId;
    private String utilityId;
    private TariffType tariffType;
    private String status;
    private LocalDateTime requestedAt;
}