package com.chubb.auth.service;

import java.util.List;

import com.chubb.auth.dto.ChangePasswordRequest;
import com.chubb.auth.dto.ForgotPasswordRequest;
import com.chubb.auth.dto.JwtResponse;
import com.chubb.auth.dto.LoginRequest;
import com.chubb.auth.dto.PendingUserResponse;
import com.chubb.auth.dto.RegisterRequest;
import com.chubb.auth.dto.ResetPasswordRequest;
import com.chubb.auth.dto.UserResponse;
import com.chubb.auth.models.User;

public interface AuthService {

    void register(RegisterRequest request);

    JwtResponse login(LoginRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(String email, ChangePasswordRequest request);

    List<User> getAllUsers();

    UserResponse getUserById(String userId);

    void deleteUser(String userId);
    
    public User activateUser(String userId);
    User rejectUser(String userId);
    
    public List<PendingUserResponse> getPendingConsumers();
    public User getByEmail(String email);
    public boolean isUserActiveByEmail(String email);
}

