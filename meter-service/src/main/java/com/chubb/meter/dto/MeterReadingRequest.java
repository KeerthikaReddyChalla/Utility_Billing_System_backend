package com.chubb.meter.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MeterReadingRequest {

    @NotBlank
    private String connectionId;
    
    @NotBlank
    private String consumerId;  

    @NotBlank
    private String utilityId;
    
    @Positive
    private double readingValue;

    @NotNull
    private LocalDate readingDate;
}
