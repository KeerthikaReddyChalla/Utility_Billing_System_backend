package com.chubb.payment.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.chubb.payment.models.PaymentOtp;

public interface PaymentOtpRepository extends MongoRepository<PaymentOtp, String> {

    Optional<PaymentOtp> findByEmail(String email);

    void deleteByEmail(String email);
}
