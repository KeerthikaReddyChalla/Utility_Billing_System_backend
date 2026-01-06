package com.chubb.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AuthUserResponse {

    private String id;
    private String name;
    private String email;
    private String role;
    private String status;
}
