package com.chubb.utility.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Document(collection = "tariffs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {

    @Id
    private String id;

    @NotNull
    private String utilityId;

    @NotNull
    private TariffType tariffType;

    @PositiveOrZero
    private double ratePerUnit;

    @PositiveOrZero
    private double fixedCharge;

    private boolean active;
}
