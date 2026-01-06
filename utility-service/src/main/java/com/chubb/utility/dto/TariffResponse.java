package com.chubb.utility.dto;

import com.chubb.utility.models.TariffType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TariffResponse {

    private String id;
    private String utilityId;
    private TariffType tariffType;
    private double ratePerUnit;
    private double fixedCharge;
    private boolean active;
}
