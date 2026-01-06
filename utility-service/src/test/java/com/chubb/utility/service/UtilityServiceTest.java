package com.chubb.utility.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chubb.utility.dto.UtilityRequest;
import com.chubb.utility.exception.ResourceConflictException;
import com.chubb.utility.exception.ResourceNotFoundException;
import com.chubb.utility.models.Tariff;
import com.chubb.utility.models.Utility;
import com.chubb.utility.repository.TariffRepository;
import com.chubb.utility.repository.UtilityRepository;

@ExtendWith(MockitoExtension.class)
class UtilityServiceTest {

    @Mock
    private UtilityRepository repository;

    @Mock
    private TariffRepository tariffRepository;

    @InjectMocks
    private UtilityService service;

    @Test
    void create_success() {
        UtilityRequest req = new UtilityRequest("Electricity", "Power");

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThat(service.create(req)).isNotNull();
    }

    @Test
    void getAll_success() {
        when(repository.findAll()).thenReturn(List.of(new Utility()));

        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    void getById_success() {
        when(repository.findById("u1"))
                .thenReturn(Optional.of(new Utility()));

        assertThat(service.getById("u1")).isNotNull();
    }

    @Test
    void delete_conflict_when_tariffs_exist() {
        when(repository.existsById("u1")).thenReturn(true);
        when(tariffRepository.findByUtilityId("u1"))
        .thenReturn(List.of(new Tariff()));

        assertThatThrownBy(() -> service.delete("u1"))
                .isInstanceOf(ResourceConflictException.class);
    }
    
    @Test
    void getById_not_found() {

        when(repository.findById("u1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById("u1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Utility not found");
    }

    @Test
    void update_success() {

        Utility utility = Utility.builder()
                .id("u1")
                .name("Old")
                .description("Old desc")
                .active(true)
                .build();

        when(repository.findById("u1"))
                .thenReturn(Optional.of(utility));

        when(repository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        UtilityRequest req =
                new UtilityRequest("New Name", "New Desc");

        var result = service.update("u1", req);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getDescription()).isEqualTo("New Desc");
    }

    @Test
    void update_not_found() {

        when(repository.findById("u1"))
                .thenReturn(Optional.empty());

        UtilityRequest req =
                new UtilityRequest("Name", "Desc");

        assertThatThrownBy(() -> service.update("u1", req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Utility not found");
    }

    @Test
    void delete_fails_when_utility_not_found() {

        when(repository.existsById("u1")).thenReturn(false);

        assertThatThrownBy(() -> service.delete("u1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Utility not found");
    }

    @Test
    void delete_success_when_no_tariffs() {

        when(repository.existsById("u1")).thenReturn(true);
        when(tariffRepository.findByUtilityId("u1"))
                .thenReturn(List.of());

        service.delete("u1");

        verify(repository).deleteById("u1");
    }

    @Test
    void map_fields_are_correct() {

        Utility utility = Utility.builder()
                .id("u1")
                .name("Electricity")
                .description("Power")
                .active(true)
                .build();

        when(repository.findById("u1"))
                .thenReturn(Optional.of(utility));

        var response = service.getById("u1");

        assertThat(response.getId()).isEqualTo("u1");
        assertThat(response.getName()).isEqualTo("Electricity");
        assertThat(response.isActive()).isTrue();
    }

}
