package com.chubb.consumer.repository;

import com.chubb.consumer.models.ConnectionRequest;
import com.chubb.consumer.models.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConnectionRequestRepository
        extends MongoRepository<ConnectionRequest, String> {

    List<ConnectionRequest> findByStatus(RequestStatus status);
}
