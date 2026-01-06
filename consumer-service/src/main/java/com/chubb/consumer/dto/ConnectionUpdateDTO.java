package com.chubb.consumer.dto;

import com.chubb.consumer.models.ConnectionStatus;

import lombok.Data;

@Data
public class ConnectionUpdateDTO {
    private ConnectionStatus status;
}
