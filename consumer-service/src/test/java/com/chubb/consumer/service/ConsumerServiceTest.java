package com.chubb.consumer.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chubb.consumer.dto.*;
import com.chubb.consumer.exception.ResourceNotFoundException;
import com.chubb.consumer.feign.AuthClient;
import com.chubb.consumer.models.Consumer;
import com.chubb.consumer.repository.ConsumerRepository;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @Mock
    private ConsumerRepository repository;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private ConsumerService service;

    @Test
    void createConsumer_success() {

    	AuthUserResponse user = AuthUserResponse.builder()
    	        .id("u1")
    	        .role("CONSUMER")
    	        .status("ACTIVE")
    	        .build();


        when(authClient.getUserById("u1")).thenReturn(user);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ConsumerRequestDTO dto = new ConsumerRequestDTO(
                "u1", "John", "john@mail.com", "9999999999", "Addr"
        );

        assertThat(service.createConsumer(dto)).isNotNull();
    }

    @Test
    void getById_not_found() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById("x"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAll_success() {
        when(repository.findAll()).thenReturn(List.of(new Consumer()));

        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    void deleteConsumer_not_found() {
        when(repository.existsById("x")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteConsumer("x"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
    
    @Test
    void createConsumer_wrongRole() {

        AuthUserResponse user = AuthUserResponse.builder()
                .id("u1")
                .role("ADMIN")
                .status("ACTIVE")
                .build();

        when(authClient.getUserById("u1"))
                .thenReturn(user);

        ConsumerRequestDTO dto =
                new ConsumerRequestDTO("u1", "John", "j@mail.com", "9", "Addr");

        assertThatThrownBy(() ->
                service.createConsumer(dto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createConsumer_notApproved() {

        AuthUserResponse user = AuthUserResponse.builder()
                .id("u1")
                .role("CONSUMER")
                .status("PENDING")
                .build();

        when(authClient.getUserById("u1"))
                .thenReturn(user);

        ConsumerRequestDTO dto =
                new ConsumerRequestDTO("u1", "John", "j@mail.com", "9", "Addr");

        assertThatThrownBy(() ->
                service.createConsumer(dto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createConsumer_alreadyExists() {

        AuthUserResponse user = AuthUserResponse.builder()
                .id("u1")
                .role("CONSUMER")
                .status("ACTIVE")
                .build();

        when(authClient.getUserById("u1"))
                .thenReturn(user);

        when(repository.existsById("u1"))
                .thenReturn(true);

        ConsumerRequestDTO dto =
                new ConsumerRequestDTO("u1", "John", "j@mail.com", "9", "Addr");

        assertThatThrownBy(() ->
                service.createConsumer(dto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deleteConsumer_success() {

        when(repository.existsById("c1"))
                .thenReturn(true);

        service.deleteConsumer("c1");

        verify(repository).deleteById("c1");
    }

    @Test
    void handleConsumerApproved_idempotent() {

        ConsumerApprovedEvent event =
                new ConsumerApprovedEvent("u1", "John", "j@mail.com", true);

        when(repository.existsById("u1"))
                .thenReturn(true);

        service.handleConsumerApproved(event);

        verify(repository, never()).save(any());
    }

    @Test
    void handleConsumerApproved_create() {

        ConsumerApprovedEvent event =
                new ConsumerApprovedEvent("u1", "John", "j@mail.com", true);

        when(repository.existsById("u1"))
                .thenReturn(false);

        service.handleConsumerApproved(event);

        verify(repository).save(any(Consumer.class));
    }

}
