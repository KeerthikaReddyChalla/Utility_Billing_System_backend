package com.chubb.consumer.dto;

import lombok.*;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ConsumerApprovedEvent {
    private String userId;
    private String name;
    private String email;
    private boolean approved;
}
