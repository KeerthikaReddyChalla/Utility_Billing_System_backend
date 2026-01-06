package com.chubb.consumer.dto;

import lombok.*;
import com.chubb.consumer.models.RequestStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ConnectionRequestUpdateDTO {
    @NotNull
    private RequestStatus status; 
}
