package com.chubb.billing.repository;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.chubb.billing.models.Bill;

public interface BillRepository extends MongoRepository<Bill, String> {
    List<Bill> findByConsumerId(String consumerId);

    
}
