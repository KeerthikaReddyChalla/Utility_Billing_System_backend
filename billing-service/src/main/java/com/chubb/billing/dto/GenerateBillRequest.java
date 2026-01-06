package com.chubb.billing.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GenerateBillRequest {

    @NotBlank
    private String connectionId;

    @NotNull
    private LocalDate billingCycle;
}
