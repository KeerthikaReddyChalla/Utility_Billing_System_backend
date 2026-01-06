package com.chubb.auth.service;

import com.chubb.auth.dto.*;
import com.chubb.auth.models.*;
import com.chubb.auth.repository.PasswordResetTokenRepository;
import com.chubb.auth.repository.UserRepository;
import com.chubb.auth.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AuthServiceImpl service;

    @BeforeEach
    void setUp() {
      
        ReflectionTestUtils.setField(service, "rabbitTemplate", rabbitTemplate);
    }

    @Test
    void register_consumer_success() {

        RegisterRequest request = RegisterRequest.builder()
                .name("John")
                .email("john@test.com")
                .password("pass")
                .role(Role.CONSUMER)
                .build();

        when(userRepository.existsByEmail("john@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("pass"))
                .thenReturn("ENC");

        service.register(request);

        verify(userRepository).save(any(User.class));
    }


    @Test
    void login_success() {

        User user = User.builder()
                .id("u1")
                .email("a@test.com")
                .password("ENC")
                .active(true)
                .build();

        when(userRepository.findByEmail("a@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("pass", "ENC"))
                .thenReturn(true);

        when(jwtUtil.generateToken(user))
                .thenReturn("JWT");

        JwtResponse response =
        		service.login(
        			    LoginRequest.builder()
        			        .email("a@test.com")
        			        .password("pass")
        			        .build()
        			);

        assertThat(response.getToken()).isEqualTo("JWT");
        assertThat(response.getUserId()).isEqualTo("u1");
    }

    @Test
    void forgotPassword_success() {

        User user = User.builder()
                .id("u1")
                .email("a@test.com")
                .build();

        when(userRepository.findByEmail("a@test.com"))
                .thenReturn(Optional.of(user));

        service.forgotPassword(
        	    ForgotPasswordRequest.builder()
        	        .email("a@test.com")
        	        .build()
        	);


        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(rabbitTemplate).convertAndSend(
                eq("notification.exchange"),
                eq("auth.forgot.password"),
                any(Object.class)
        );
    }


    @Test
    void resetPassword_success() {

        PasswordResetToken token = PasswordResetToken.builder()
                .token("t1")
                .userId("u1")
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();

        User user = User.builder()
                .id("u1")
                .password("OLD")
                .build();

        when(tokenRepository.findByToken("t1"))
                .thenReturn(Optional.of(token));

        when(userRepository.findById("u1"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("NEW"))
                .thenReturn("ENC");

        service.resetPassword(
        	    ResetPasswordRequest.builder()
        	        .token("t1")
        	        .newPassword("NEW")
        	        .build()
        	);


        verify(userRepository).save(user);
        verify(tokenRepository).deleteByUserId("u1");
    }


    @Test
    void getPendingConsumers_success() {

        User u = User.builder()
                .id("u1")
                .name("John")
                .email("a@test.com")
                .build();

        when(userRepository.findByRoleAndActive(Role.CONSUMER, false))
                .thenReturn(List.of(u));

        List<PendingUserResponse> result =
                service.getPendingConsumers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("a@test.com");
    }


    @Test
    void activateUser_success() {

        User user = User.builder()
                .id("u1")
                .active(false)
                .build();

        when(userRepository.findById("u1"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        User activated = service.activateUser("u1");

        assertThat(activated.isActive()).isTrue();
    }
    @Test
    void register_userAlreadyExists() {

        RegisterRequest request = RegisterRequest.builder()
                .email("john@test.com")
                .build();

        when(userRepository.existsByEmail("john@test.com"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(ResponseStatusException.class);
    }
    
    @Test
    void login_inactiveUser() {

        User user = User.builder()
                .email("a@test.com")
                .password("ENC")
                .active(false)
                .build();

        when(userRepository.findByEmail("a@test.com"))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                service.login(
                        LoginRequest.builder()
                                .email("a@test.com")
                                .password("pass")
                                .build()
                )
        ).isInstanceOf(ResponseStatusException.class);
    }
    
    @Test
    void login_wrongPassword() {

        User user = User.builder()
                .email("a@test.com")
                .password("ENC")
                .active(true)
                .build();

        when(userRepository.findByEmail("a@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("pass", "ENC"))
                .thenReturn(false);

        assertThatThrownBy(() ->
                service.login(
                        LoginRequest.builder()
                                .email("a@test.com")
                                .password("pass")
                                .build()
                )
        ).isInstanceOf(ResponseStatusException.class);
    }
    
    @Test
    void changePassword_success() {

        User user = User.builder()
                .email("a@test.com")
                .password("OLD")
                .build();

        when(userRepository.findByEmail("a@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("old", "OLD"))
                .thenReturn(true);

        when(passwordEncoder.encode("new"))
                .thenReturn("ENC");

        service.changePassword(
                "a@test.com",
                ChangePasswordRequest.builder()
                        .oldPassword("old")
                        .newPassword("new")
                        .build()
        );

        verify(userRepository).save(user);
    }

    @Test
    void getAllUsers_success() {

        when(userRepository.findAll())
                .thenReturn(List.of(new User()));

        List<User> users = service.getAllUsers();

        assertThat(users).hasSize(1);
    }
    
    @Test
    void deleteUser_success() {

        service.deleteUser("u1");

        verify(userRepository).deleteById("u1");
    }

    @Test
    void getUserById_success() {

        User user = User.builder()
                .id("u1")
                .name("John")
                .email("a@test.com")
                .role(Role.ADMIN)
                .active(true)
                .build();

        when(userRepository.findById("u1"))
                .thenReturn(Optional.of(user));

        UserResponse response = service.getUserById("u1");

        assertThat(response.getEmail()).isEqualTo("a@test.com");
    }

    @Test
    void isUserActiveByEmail_true() {

        User user = User.builder()
                .active(true)
                .build();

        when(userRepository.findByEmail("a@test.com"))
                .thenReturn(Optional.of(user));

        assertThat(service.isUserActiveByEmail("a@test.com")).isTrue();
    }

    @Test
    void isUserActiveByEmail_falseWhenMissing() {

        when(userRepository.findByEmail("missing@test.com"))
                .thenReturn(Optional.empty());

        assertThat(service.isUserActiveByEmail("missing@test.com")).isFalse();
    }

}
