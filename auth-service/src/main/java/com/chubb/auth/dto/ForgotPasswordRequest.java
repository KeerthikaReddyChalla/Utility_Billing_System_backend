package com.chubb.auth.dto;

import lombok.*;
import lombok.Builder;
import jakarta.validation.constraints.Email;
import lombok.Data;



@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ForgotPasswordRequest {

    @Email
    private String email;
}
