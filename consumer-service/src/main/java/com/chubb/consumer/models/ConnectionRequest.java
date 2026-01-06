package com.chubb.consumer.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "connection_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRequest {

    @Id
    private String id;

    private String consumerId;
    private String utilityId;
    private TariffType tariffType;

    private RequestStatus status;

    private LocalDateTime requestedAt;
}
