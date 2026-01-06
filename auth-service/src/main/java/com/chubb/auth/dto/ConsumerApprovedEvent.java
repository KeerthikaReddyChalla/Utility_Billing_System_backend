package com.chubb.auth.dto;

import lombok.Data;

@Data
public class ConsumerApprovedEvent {
    private String userId;
    private String name;
    private String email;
    private boolean approved;
}
