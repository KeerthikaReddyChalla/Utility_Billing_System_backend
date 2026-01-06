package com.chubb.auth.service;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chubb.auth.dto.ChangePasswordRequest;
import com.chubb.auth.dto.ForgotPasswordEvent;
import com.chubb.auth.dto.ForgotPasswordRequest;
import com.chubb.auth.dto.JwtResponse;
import com.chubb.auth.dto.LoginRequest;
import com.chubb.auth.dto.PendingUserResponse;
import com.chubb.auth.dto.RegisterRequest;
import com.chubb.auth.dto.ResetPasswordRequest;
import com.chubb.auth.dto.UserResponse;
import com.chubb.auth.models.PasswordResetToken;
import com.chubb.auth.models.Role;
import com.chubb.auth.models.User;
import com.chubb.auth.repository.PasswordResetTokenRepository;
import com.chubb.auth.repository.UserRepository;
import com.chubb.auth.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
        	throw new ResponseStatusException(
        	        HttpStatus.BAD_REQUEST,
        	        "User already exists"
        	);
        }
        boolean isConsumer = request.getRole() == Role.CONSUMER;

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(!isConsumer)
                .build();

        userRepository.save(user);
    }

    @Override
    public JwtResponse login(LoginRequest request) {

    	User user = userRepository.findByEmail(request.getEmail())
    	        .orElseThrow(() -> new ResponseStatusException(
    	                HttpStatus.UNAUTHORIZED,
    	                "Invalid credentials"
    	        ));

        
        if (!user.isActive()) {
        	throw new ResponseStatusException(
        	        HttpStatus.FORBIDDEN,
        	        "Account pending admin approval"
        	);

        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        	throw new ResponseStatusException(
        	        HttpStatus.UNAUTHORIZED,
        	        "Invalid credentials"
        	);

        }

        return new JwtResponse(jwtUtil.generateToken(user), user.getId());
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        
     

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .userId(user.getId())
                .token(token)
                .expiryTime(LocalDateTime.now().plusMinutes(30))
                .build();

        tokenRepository.deleteByUserId(user.getId());
        tokenRepository.save(resetToken);
        ForgotPasswordEvent event =
                new ForgotPasswordEvent(user.getEmail(), token);

        rabbitTemplate.convertAndSend(
                "notification.exchange",
                "auth.forgot.password",
                event
        );
  
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

    	PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
    	        .orElseThrow(() -> new ResponseStatusException(
    	                HttpStatus.BAD_REQUEST,
    	                "Invalid reset token"
    	        ));


        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
        	throw new ResponseStatusException(
        	        HttpStatus.BAD_REQUEST,
        	        "Reset token expired"
        	);

        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));


        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.deleteByUserId(user.getId());
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));


        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
        	throw new ResponseStatusException(
        	        HttpStatus.BAD_REQUEST,
        	        "Old password is incorrect"
        	);

        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserResponse getUserById(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));


        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.isActive())
                .build();
    }


    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
    
    @Override
    public User activateUser(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));


        user.setActive(true);
        return userRepository.save(user);
    }
    
    @Override
    public User rejectUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));


        user.setActive(false);
        return userRepository.save(user);
    }
    

    public List<PendingUserResponse> getPendingConsumers() {

        return userRepository
                .findByRoleAndActive(Role.CONSUMER, false)
                .stream()
                .map(user -> PendingUserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .toList();
    }
    public User getByEmail(String email) {
    	return userRepository.findByEmail(email)
    	        .orElseThrow(() ->
    	            new ResponseStatusException(
    	                HttpStatus.NOT_FOUND,
    	                "User not found with email: " + email
    	            )
    	        );
    }
    public boolean isUserActiveByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::isActive)
                .orElse(false);
    }

    


}
