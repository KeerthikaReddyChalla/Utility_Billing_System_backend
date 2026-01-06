package com.chubb.billing.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UtilityResponse {
    private String id;
    private String name;
}
