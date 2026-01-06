package com.chubb.consumer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumerResponseDTO {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
}

