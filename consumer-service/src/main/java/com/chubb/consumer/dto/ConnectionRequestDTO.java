package com.chubb.consumer.dto;

import lombok.AllArgsConstructor;

import com.chubb.consumer.models.TariffType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.Builder;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ConnectionRequestDTO {

    @NotBlank
    private String consumerId;

    @NotBlank
    private String utilityId;

    @NotNull
    private TariffType tariffType; 
}
