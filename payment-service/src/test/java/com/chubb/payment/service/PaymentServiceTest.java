package com.chubb.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.chubb.payment.client.BillingClient;
import com.chubb.payment.dto.OtpEmailEvent;
import com.chubb.payment.dto.PaymentRequest;
import com.chubb.payment.models.Payment;
import com.chubb.payment.models.PaymentOtp;
import com.chubb.payment.models.PaymentStatus;
import com.chubb.payment.repository.PaymentOtpRepository;
import com.chubb.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository repository;

    @Mock
    private BillingClient billingClient;

    @Mock
    private PaymentOtpRepository otpRepo;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentService service;

    @Test
    void sendOtp_success() {
        service.sendOtp("test@gmail.com");

        verify(otpRepo).save(any());
        verify(rabbitTemplate).convertAndSend(
        	    eq("payment.exchange"),
        	    eq("payment.otp"),
        	    any(OtpEmailEvent.class)
        	);
    }

    @Test
    void verifyOtpAndCompletePayment_success() {

        PaymentOtp otp = PaymentOtp.builder()
                .email("test@gmail.com")
                .otp("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        when(otpRepo.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(otp));

        when(repository.save(any()))
                .thenAnswer(i -> {
                    Payment p = i.getArgument(0);
                    p.setId("p1");
                    return p;
                });

        PaymentRequest req = PaymentRequest.builder()
                .email("test@gmail.com")
                .otp("123456")
                .billId("b1")
                .consumerId("c1")
                .amount(100)
                .build();

        var response = service.verifyOtpAndCompletePayment(req);

        assertEquals("COMPLETED", response.getStatus());
        verify(billingClient).markBillAsPaid("b1");
        verify(otpRepo).deleteByEmail("test@gmail.com");
    }
    
    @Test
    void verifyOtp_fails_when_otp_not_found() {

        when(otpRepo.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        PaymentRequest req = PaymentRequest.builder()
                .email("test@gmail.com")
                .otp("123456")
                .billId("b1")
                .consumerId("c1")
                .amount(100)
                .build();

        var ex = assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> service.verifyOtpAndCompletePayment(req)
        );

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void verifyOtp_fails_when_otp_expired() {

        PaymentOtp otp = PaymentOtp.builder()
                .email("test@gmail.com")
                .otp("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(1)) // expired
                .build();

        when(otpRepo.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(otp));

        PaymentRequest req = PaymentRequest.builder()
                .email("test@gmail.com")
                .otp("123456")
                .billId("b1")
                .consumerId("c1")
                .amount(100)
                .build();

        var ex = assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> service.verifyOtpAndCompletePayment(req)
        );

        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void verifyOtp_fails_when_invalid_otp() {

        PaymentOtp otp = PaymentOtp.builder()
                .email("test@gmail.com")
                .otp("999999")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        when(otpRepo.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(otp));

        PaymentRequest req = PaymentRequest.builder()
                .email("test@gmail.com")
                .otp("123456") // wrong
                .billId("b1")
                .consumerId("c1")
                .amount(100)
                .build();

        var ex = assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> service.verifyOtpAndCompletePayment(req)
        );

        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void getById_success() {

        Payment payment = Payment.builder()
                .id("p1")
                .billId("b1")
                .amount(100)
                .status(com.chubb.payment.models.PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .build();

        when(repository.findById("p1"))
                .thenReturn(Optional.of(payment));

        var response = service.getById("p1");

        assertEquals("p1", response.getPaymentId());
    }

    @Test
    void getById_not_found() {

        when(repository.findById("x"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> service.getById("x")
        );
    }

    @Test
    void getAllPayments_success() {

        when(repository.findAll())
                .thenReturn(List.of(
                        Payment.builder()
                                .id("p1")
                                .billId("b1")
                                .amount(100)
                                .status(com.chubb.payment.models.PaymentStatus.COMPLETED)
                                .paidAt(LocalDateTime.now())
                                .build()
                ));

        assertEquals(1, service.getAllPayments().size());
    }

    @Test
    void getByBill_success() {

        Payment payment = Payment.builder()
                .id("p1")
                .billId("b1")
                .consumerId("c1")
                .amount(100)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .build();

        when(repository.findByBillId("b1"))
                .thenReturn(List.of(payment));

        assertEquals(1, service.getByBill("b1").size());
    }


    @Test
    void getByConsumer_success() {

        Payment payment = Payment.builder()
                .id("p1")
                .billId("b1")
                .consumerId("c1")
                .amount(100)
                .status(PaymentStatus.COMPLETED) 
                .paidAt(LocalDateTime.now())
                .build();

        when(repository.findByConsumerId("c1"))
                .thenReturn(List.of(payment));

        assertEquals(1, service.getByConsumer("c1").size());
    }


}
