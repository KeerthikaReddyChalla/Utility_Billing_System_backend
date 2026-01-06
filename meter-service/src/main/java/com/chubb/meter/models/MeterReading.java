package com.chubb.meter.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "meter_readings")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MeterReading {

    @Id
    private String id;

    private String connectionId;
    private String consumerId;
    private String utilityId;
    private double readingValue;

    private LocalDate readingDate;

    private LocalDateTime createdAt;
}
