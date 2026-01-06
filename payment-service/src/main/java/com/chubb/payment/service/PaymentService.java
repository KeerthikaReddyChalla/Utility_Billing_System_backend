package com.chubb.payment.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.chubb.payment.client.BillingClient;
import com.chubb.payment.dto.PaymentRequest;
import com.chubb.payment.dto.PaymentResponse;
import com.chubb.payment.models.Payment;
import com.chubb.payment.models.PaymentOtp;
import com.chubb.payment.models.PaymentStatus;
import com.chubb.payment.repository.PaymentOtpRepository;
import com.chubb.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import com.chubb.payment.dto.OtpEmailEvent;
@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository repository;
    private final BillingClient billingClient;
    private final PaymentOtpRepository otpRepo;
    private final RabbitTemplate rabbitTemplate;

    public void sendOtp(String email) {

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        PaymentOtp paymentOtp = PaymentOtp.builder()
                .email(email)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        otpRepo.deleteByEmail(email);
        otpRepo.save(paymentOtp);


        rabbitTemplate.convertAndSend(
                "payment.exchange",
                "payment.otp",
                new OtpEmailEvent(email, otp)
        );
    }

 

    public PaymentResponse verifyOtpAndCompletePayment(PaymentRequest req) {

    	PaymentOtp otp = otpRepo.findByEmail(req.getEmail())
    	        .orElseThrow(() -> new ResponseStatusException(
    	                HttpStatus.NOT_FOUND,
    	                "OTP not found"
    	        ));

    	if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
    	    throw new ResponseStatusException(
    	            HttpStatus.BAD_REQUEST,
    	            "OTP expired"
    	    );
    	}

    	if (!otp.getOtp().equals(req.getOtp())) {
    	    throw new ResponseStatusException(
    	            HttpStatus.BAD_REQUEST,
    	            "Invalid OTP"
    	    );
    	}

        Payment payment = Payment.builder()
                .billId(req.getBillId())
                .consumerId(req.getConsumerId())
                .amount(req.getAmount())
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .build();

        Payment saved = repository.save(payment);

        billingClient.markBillAsPaid(req.getBillId());

        otpRepo.deleteByEmail(req.getEmail());

        return map(saved);
    }

    public List<PaymentResponse> getAllPayments() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    private PaymentResponse map(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getId())
                .billId(p.getBillId())
                .amount(p.getAmount())
                .status(p.getStatus().name())
                .paymentDate(p.getPaidAt())
                .build();
    }
    

    public PaymentResponse getById(String id) {
        return repository.findById(id)
                .map(this::map)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public List<PaymentResponse> getByBill(String billId) {
        return repository.findByBillId(billId).stream().map(this::map).toList();
    }

    public List<PaymentResponse> getByConsumer(String consumerId) {
        return repository.findByConsumerId(consumerId).stream().map(this::map).toList();
    }

  
}
