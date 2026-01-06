package com.chubb.utility.dto;

import lombok.Builder;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TariffRequest {

    @NotBlank
    private String utilityId;

    @NotBlank
    private String name;

    @Positive
    private double ratePerUnit;

    @NotNull
    private LocalDate effectiveFrom;
}
