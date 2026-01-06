package com.chubb.meter.feign;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConnectionDTO {
	
	@JsonProperty("_id")
    private String id;
	
    private String status;
}
