package com.chubb.consumer.dto;

import com.chubb.consumer.models.TariffType;
import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
@Builder
public class ConnectionResponseDTO {
	
	@JsonProperty("_id")
	
    private String id;
    private String consumerId;
    private String utilityId;
    private TariffType tariffType;   
    private String status;
}
