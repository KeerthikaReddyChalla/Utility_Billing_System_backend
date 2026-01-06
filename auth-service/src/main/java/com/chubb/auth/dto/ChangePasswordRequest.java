package com.chubb.auth.dto;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Builder
@Data
public class ChangePasswordRequest {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}

