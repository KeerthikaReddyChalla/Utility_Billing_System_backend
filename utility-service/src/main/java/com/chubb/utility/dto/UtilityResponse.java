package com.chubb.utility.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UtilityResponse {
    private String id;
    private String name;
    private String description;
    private boolean active;
}
