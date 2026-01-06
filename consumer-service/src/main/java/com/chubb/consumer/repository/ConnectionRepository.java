package com.chubb.consumer.repository;

import com.chubb.consumer.models.Connection;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConnectionRepository extends MongoRepository<Connection, String> {
    List<Connection> findByConsumerId(String consumerId);
    
}

