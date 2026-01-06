package com.chubb.consumer.repository;

import com.chubb.consumer.models.Consumer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConsumerRepository extends MongoRepository<Consumer, String> {
    Optional<Consumer> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
