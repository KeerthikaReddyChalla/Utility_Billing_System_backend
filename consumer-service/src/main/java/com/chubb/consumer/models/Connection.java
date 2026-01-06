package com.chubb.consumer.models;
import lombok.*;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;


@Document(collection = "connections")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Connection {

    @Id
    private String id;

    private String consumerId;
    private String utilityId;
    private TariffType tariffType;  
    
    @Enumerated(EnumType.STRING)
    private ConnectionStatus status;

    private LocalDateTime requestedAt;
    private LocalDateTime activatedAt;
}
