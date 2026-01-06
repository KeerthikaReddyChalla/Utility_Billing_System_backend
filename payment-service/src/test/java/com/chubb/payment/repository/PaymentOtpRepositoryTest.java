package com.chubb.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.chubb.payment.models.PaymentOtp;
import org.junit.jupiter.api.BeforeEach;

@DataMongoTest
class PaymentOtpRepositoryTest {

    @Autowired
    private PaymentOtpRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void findByEmail_success() {

        repository.save(
                PaymentOtp.builder()
                        .email("test@gmail.com")
                        .otp("123456")
                        .expiresAt(LocalDateTime.now().plusMinutes(5))
                        .build()
        );

        assertThat(repository.findByEmail("test@gmail.com")).isPresent();
    }
}
