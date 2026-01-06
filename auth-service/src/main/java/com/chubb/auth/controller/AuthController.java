package com.chubb.auth.controller;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.chubb.auth.dto.ChangePasswordRequest;
import com.chubb.auth.dto.ConsumerApprovedEvent;
import com.chubb.auth.dto.ForgotPasswordRequest;
import com.chubb.auth.dto.JwtResponse;
import com.chubb.auth.dto.LoginRequest;
import com.chubb.auth.dto.PendingUserResponse;
import com.chubb.auth.dto.RegisterRequest;
import com.chubb.auth.dto.ResetPasswordRequest;
import com.chubb.auth.dto.UserResponse;
import com.chubb.auth.models.User;
import com.chubb.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RabbitTemplate rabbitTemplate;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
    }

    @PutMapping("/change-password")
    public void changePassword(@AuthenticationPrincipal UserDetails userDetails,
                               @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userDetails.getUsername(), request);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return authService.getAllUsers();
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','BILLING_OFFICER')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable("userId") String userId) {
        return ResponseEntity.ok(authService.getUserById(userId));
    }


    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable("id") String id) {
        authService.deleteUser(id);
    }
   
    @GetMapping("/users/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PendingUserResponse>> getPendingUsers() {
        return ResponseEntity.ok(authService.getPendingConsumers());
    }
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public User getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return authService.getByEmail(userDetails.getUsername());
    }
    
    @PutMapping("/users/{userId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveUser(@PathVariable("userId") String userId) {

        User user = authService.activateUser(userId);

        ConsumerApprovedEvent event = new ConsumerApprovedEvent();
        event.setUserId(user.getId());
        event.setEmail(user.getEmail());
        event.setName(user.getName());
        event.setApproved(true);

        rabbitTemplate.convertAndSend(
                "utility.events.exchange",
                "auth.consumer.approved",
                event
        );
    

        return ResponseEntity.ok().build();
    }
    @PutMapping("/users/{userId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectUser(@PathVariable("userId") String userId) {

        User user = authService.rejectUser(userId); // sets active=false

        ConsumerApprovedEvent event = new ConsumerApprovedEvent();
        event.setUserId(user.getId());
        event.setEmail(user.getEmail());
        event.setName(user.getName());
        event.setApproved(false);

        rabbitTemplate.convertAndSend(
                "utility.events.exchange",
                "auth.consumer.approved",
                event
        );
 

        return ResponseEntity.ok().build();
    }
    @GetMapping("/pending-status")
    public ResponseEntity<Boolean> getPendingStatus(@RequestParam("email") String email) {
        boolean approved = authService.isUserActiveByEmail(email);
        return ResponseEntity.ok(approved);
    }






}
