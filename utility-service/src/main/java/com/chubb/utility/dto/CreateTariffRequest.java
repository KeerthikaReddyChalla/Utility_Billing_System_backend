package com.chubb.utility.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.chubb.utility.models.TariffType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateTariffRequest {

    @NotNull
    private String utilityId;

    @NotNull
    private TariffType tariffType;

    @PositiveOrZero
    private double ratePerUnit;

    @PositiveOrZero
    private double fixedCharge;
}
