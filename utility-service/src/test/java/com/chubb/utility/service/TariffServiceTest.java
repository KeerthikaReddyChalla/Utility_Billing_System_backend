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

import com.chubb.utility.dto.CreateTariffRequest;
import com.chubb.utility.dto.UpdateTariffRequest;
import com.chubb.utility.exception.ResourceNotFoundException;
import com.chubb.utility.models.Tariff;
import com.chubb.utility.models.TariffType;
import com.chubb.utility.repository.TariffRepository;
import com.chubb.utility.repository.UtilityRepository;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private UtilityRepository utilityRepository;

    @InjectMocks
    private TariffService service;

    @Test
    void create_success() {

        CreateTariffRequest req = new CreateTariffRequest(
                "u1",
                TariffType.RESIDENTIAL_FLAT,
                5.0,
                50.0
        );

        when(utilityRepository.existsById("u1")).thenReturn(true);
        when(tariffRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThat(service.create(req)).isNotNull();
    }

    @Test
    void getAll_success() {
        when(tariffRepository.findAll()).thenReturn(List.of(new Tariff()));

        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    void getRateByUtilityId_success() {
        Tariff tariff = Tariff.builder()
                .ratePerUnit(6.0)
                .active(true)
                .build();

        when(tariffRepository.findFirstByUtilityIdAndActiveTrue("u1"))
                .thenReturn(Optional.of(tariff));

        assertThat(service.getRateByUtilityId("u1")).isEqualTo(6.0);
    }
    
    @Test
    void create_fails_when_utility_not_found() {

        CreateTariffRequest req = new CreateTariffRequest(
                "u1",
                TariffType.RESIDENTIAL_FLAT,
                5.0,
                50.0
        );

        when(utilityRepository.existsById("u1")).thenReturn(false);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Utility not found");
    }

    @Test
    void update_success() {

        Tariff tariff = Tariff.builder()
                .id("t1")
                .ratePerUnit(5.0)
                .fixedCharge(50.0)
                .build();

        when(tariffRepository.findById("t1"))
                .thenReturn(Optional.of(tariff));

        when(tariffRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        var req = new UpdateTariffRequest(6.0, 60.0);

        var result = service.update("t1", req);

        assertThat(result.getRatePerUnit()).isEqualTo(6.0);
        assertThat(result.getFixedCharge()).isEqualTo(60.0);
    }
    
    @Test
    void update_fails_when_tariff_not_found() {

        when(tariffRepository.findById("t1"))
                .thenReturn(Optional.empty());

        var req = new UpdateTariffRequest(6.0, 60.0);

        assertThatThrownBy(() -> service.update("t1", req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tariff not found");
    }

    @Test
    void getByUtility_success() {

        when(utilityRepository.existsById("u1")).thenReturn(true);
        when(tariffRepository.findByUtilityId("u1"))
                .thenReturn(List.of(
                        Tariff.builder()
                                .utilityId("u1")
                                .tariffType(TariffType.RESIDENTIAL_FLAT)
                                .ratePerUnit(5.0)
                                .build()
                ));

        var result = service.getByUtility("u1");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByUtility_fails_when_utility_not_found() {

        when(utilityRepository.existsById("u1")).thenReturn(false);

        assertThatThrownBy(() -> service.getByUtility("u1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Utility not found");
    }

    @Test
    void getById_success() {

        Tariff tariff = Tariff.builder()
                .id("t1")
                .utilityId("u1")
                .ratePerUnit(5.0)
                .active(true)
                .build();

        when(tariffRepository.findById("t1"))
                .thenReturn(Optional.of(tariff));

        var result = service.getById("t1");

        assertThat(result.getId()).isEqualTo("t1");
    }

    @Test
    void getById_not_found() {

        when(tariffRepository.findById("t1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById("t1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tariff not found");
    }

    @Test
    void delete_success() {

        Tariff tariff = Tariff.builder().id("t1").build();

        when(tariffRepository.findById("t1"))
                .thenReturn(Optional.of(tariff));

        service.delete("t1");

        verify(tariffRepository).delete(tariff);
    }

    @Test
    void delete_fails_when_tariff_not_found() {

        when(tariffRepository.findById("t1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete("t1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tariff not found");
    }


    
}
