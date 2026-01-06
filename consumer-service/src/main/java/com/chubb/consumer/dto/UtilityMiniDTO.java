package com.chubb.consumer.dto;

import lombok.Data;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UtilityMiniDTO {
    private String id;
    private String name;
}
