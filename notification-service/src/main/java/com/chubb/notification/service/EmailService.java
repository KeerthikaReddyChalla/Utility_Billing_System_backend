package com.chubb.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

    }
    public void sendOtpMail(String to, String otp) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("UtilityBill – Payment OTP");
        msg.setText("Your OTP for completing payment is: " + otp);

        mailSender.send(msg);
    }
    public void send(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
    public void sendResetLink(String toEmail, String token) {

        String resetLink =
                "http://localhost:4200/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset your UtilityBilling password");
        message.setText(
                "You requested to reset your password.\n\n" +
                "Click the link below to reset it:\n" +
                resetLink + "\n\n" +
                "This link is valid for 30 minutes.\n\n" +
                "If you did not request this, please ignore this email."
        );

        mailSender.send(message);
    }
}
