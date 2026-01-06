package com.chubb.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PendingUserResponse {

    private String id;
    private String name;
    private String email;
}
