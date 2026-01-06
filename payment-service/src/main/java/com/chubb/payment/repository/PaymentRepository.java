package com.chubb.payment.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.chubb.payment.models.Payment;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByBillId(String billId);

    List<Payment> findByConsumerId(String consumerId);
}
