package com.chubb.billing.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConsumerResponse {
    private String id;
    private String fullName;
    private String email;
}
