package com.chubb.auth.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;



@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ResetPasswordRequest {

    @NotBlank
    private String token;

    @NotBlank
    private String newPassword;
}
