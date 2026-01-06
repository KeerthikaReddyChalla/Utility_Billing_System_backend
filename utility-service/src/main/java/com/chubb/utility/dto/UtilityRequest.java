package com.chubb.utility.dto;

import lombok.*;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.AllArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UtilityRequest {
    @NotBlank
    private String name;
    private String description;
}
