package com.chubb.consumer.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "consumers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Consumer {

    @Id
    private String id;

    private String userId; 
    private String fullName;
    private String email;
    private String phone;
    private String address;
}
