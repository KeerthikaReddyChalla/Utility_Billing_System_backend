package com.chubb.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.chubb.payment.models.Payment;
import com.chubb.payment.models.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;

@DataMongoTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void findByBillId_success() {

        repository.save(
                Payment.builder()
                        .billId("b1")
                        .consumerId("c1")
                        .amount(100)
                        .status(PaymentStatus.COMPLETED)
                        .paidAt(LocalDateTime.now())
                        .build()
        );

        List<Payment> result = repository.findByBillId("b1");

        assertThat(result).hasSize(1);
    }
}
