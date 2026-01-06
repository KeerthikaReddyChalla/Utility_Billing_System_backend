package com.chubb.auth.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginRequest {

    @Email
    private String email;

    @NotBlank
    private String password;
}
