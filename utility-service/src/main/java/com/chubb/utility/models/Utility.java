package com.chubb.utility.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Document(collection = "utilities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Utility {

    @Id
    private String id;

    @NotBlank
    private String name;

    private String description;

    private boolean active;

    private LocalDateTime createdAt;
}
