package com.chubb.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDTO {
    private String consumerId;
    private String type;
    private String message;
}
