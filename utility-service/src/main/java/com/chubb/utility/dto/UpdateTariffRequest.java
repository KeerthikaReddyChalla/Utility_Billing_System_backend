package com.chubb.utility.dto;

import lombok.Builder;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateTariffRequest {

    @PositiveOrZero
    private double ratePerUnit;

    @PositiveOrZero
    private double fixedCharge;
}
