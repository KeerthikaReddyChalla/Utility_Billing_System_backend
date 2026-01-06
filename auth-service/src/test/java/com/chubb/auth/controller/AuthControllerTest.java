package com.chubb.auth.controller;

import com.chubb.auth.dto.*;
import com.chubb.auth.models.Role;
import com.chubb.auth.models.User;
import com.chubb.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) 
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    private User mockUser() {
        User user = new User();
        user.setId("U1");
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setActive(true);
        return user;
    }


    @Test
    void register_success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@test.com");
        req.setPassword("password");
        req.setName("Test");
        req.setRole(Role.CONSUMER);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void login_success() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");
        req.setPassword("password");

        Mockito.when(authService.login(Mockito.any()))
                .thenReturn(new JwtResponse("token", "U1"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void forgotPassword_success() throws Exception {
        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("test@test.com");

        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void resetPassword_success() throws Exception {
        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("token");
        req.setNewPassword("newPassword");

        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers_success() throws Exception {
        Mockito.when(authService.getAllUsers())
                .thenReturn(List.of(mockUser()));

        mockMvc.perform(get("/auth/users"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_success() throws Exception {
        Mockito.when(authService.getUserById("U1"))
                .thenReturn(UserResponse.builder().id("U1").build());

        mockMvc.perform(get("/auth/users/U1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_success() throws Exception {
        mockMvc.perform(delete("/auth/users/U1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getPendingUsers_success() throws Exception {
        Mockito.when(authService.getPendingConsumers())
                .thenReturn(List.of());

        mockMvc.perform(get("/auth/users/pending"))
                .andExpect(status().isOk());
    }

    @Test
    void approveUser_success() throws Exception {
        Mockito.when(authService.activateUser("U1"))
                .thenReturn(mockUser());

        mockMvc.perform(put("/auth/users/U1/approve"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectUser_success() throws Exception {
        Mockito.when(authService.rejectUser("U1"))
                .thenReturn(mockUser());

        mockMvc.perform(put("/auth/users/U1/reject"))
                .andExpect(status().isOk());
    }

    @Test
    void pendingStatus_success() throws Exception {
        Mockito.when(authService.isUserActiveByEmail("test@test.com"))
                .thenReturn(true);

        mockMvc.perform(get("/auth/pending-status")
                .param("email", "test@test.com"))
                .andExpect(status().isOk());
    }
}
