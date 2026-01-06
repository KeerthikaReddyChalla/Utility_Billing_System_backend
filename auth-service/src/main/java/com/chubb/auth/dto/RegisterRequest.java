package com.chubb.auth.dto;
import lombok.Data;
import lombok.*;
import lombok.Builder;

import com.chubb.auth.models.Role;

import jakarta.validation.constraints.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RegisterRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Role role;
}
