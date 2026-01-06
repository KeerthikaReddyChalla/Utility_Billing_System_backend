package com.chubb.meter.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MeterReadingResponse {

    private String id;
    private String connectionId;
    private double readingValue;
    private String consumerId;   
    private String utilityId;  
    private LocalDate readingDate;
    private LocalDateTime createdAt;
}
