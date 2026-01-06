package com.chubb.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String id;
    private String name;
    private String email;
    private String role;
    private boolean active;
}
