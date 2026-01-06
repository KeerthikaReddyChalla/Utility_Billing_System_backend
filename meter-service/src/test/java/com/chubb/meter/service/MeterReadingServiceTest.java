package com.chubb.meter.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chubb.meter.dto.MeterReadingRequest;
import com.chubb.meter.dto.MeterReadingResponse;
import com.chubb.meter.exception.DuplicateReadingException;
import com.chubb.meter.exception.InvalidConnectionStateException;
import com.chubb.meter.exception.ResourceNotFoundException;
import com.chubb.meter.feign.ConnectionClient;
import com.chubb.meter.feign.ConnectionDTO;
import com.chubb.meter.models.MeterReading;
import com.chubb.meter.repository.MeterReadingRepository;

/**
 * Pure unit tests – NO Spring context
 */
@ExtendWith(MockitoExtension.class)
class MeterReadingServiceTest {

    @Mock
    private MeterReadingRepository repository;

    @Mock
    private ConnectionClient connectionClient;

    @InjectMocks
    private MeterReadingService service;

    @Test
    void create_success() {

   
    	ConnectionDTO connection = new ConnectionDTO();
    	connection.setId("c1");
    	connection.setStatus("ACTIVE");

    	when(connectionClient.getConnection("c1")).thenReturn(connection);
        when(repository.existsByConnectionIdAndReadingDate(any(), any()))
                .thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        MeterReadingRequest request = MeterReadingRequest.builder()
                .connectionId("c1")
                .consumerId("cons1")
                .utilityId("u1")
                .readingValue(123.0)
                .readingDate(LocalDate.now())
                .build();

        assertThat(service.create(request)).isNotNull();
    }

    @Test
    void create_fails_when_connection_not_active() {

    	ConnectionDTO connection = new ConnectionDTO();
    	connection.setId("c1");
    	connection.setStatus("INACTIVE");

    	when(connectionClient.getConnection("c1")).thenReturn(connection);


        MeterReadingRequest request = MeterReadingRequest.builder()
                .connectionId("c1")
                .readingDate(LocalDate.now())
                .build();

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(InvalidConnectionStateException.class);
    }

    @Test
    void create_fails_when_duplicate_reading() {

    	ConnectionDTO connection = new ConnectionDTO();
    	connection.setId("c1");
    	connection.setStatus("ACTIVE");

    	when(connectionClient.getConnection("c1")).thenReturn(connection);

        when(repository.existsByConnectionIdAndReadingDate(any(), any()))
                .thenReturn(true);

        MeterReadingRequest request = MeterReadingRequest.builder()
                .connectionId("c1")
                .readingDate(LocalDate.now())
                .build();

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateReadingException.class);
    }

    @Test
    void getLatest_success() {

        when(repository.findTopByConnectionIdOrderByReadingDateDesc("c1"))
                .thenReturn(Optional.of(new MeterReading()));

        assertThat(service.getLatest("c1")).isNotNull();
    }

    @Test
    void getPrevious_returns_null_when_only_one_reading() {

        when(repository.findTop2ByConnectionIdOrderByReadingDateDesc("c1"))
                .thenReturn(List.of(new MeterReading()));

        assertThat(service.getPrevious("c1")).isNull();
    }

    @Test
    void getAllConnectionIdsWithReadings_success() {

        when(repository.findAll()).thenReturn(
                List.of(
                        MeterReading.builder().connectionId("c1").build(),
                        MeterReading.builder().connectionId("c2").build()
                )
        );

        assertThat(service.getAllConnectionIdsWithReadings())
                .containsExactlyInAnyOrder("c1", "c2");
    }
    
    @Test
    void getLatest_throws_when_not_found() {

        when(repository.findTopByConnectionIdOrderByReadingDateDesc("c1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getLatest("c1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByConnection_success() {

        MeterReading r = MeterReading.builder()
                .id("m1")
                .connectionId("c1")
                .consumerId("cons1")
                .utilityId("u1")
                .readingValue(100)
                .readingDate(LocalDate.now())
                .build();

        when(repository.findByConnectionId("c1"))
                .thenReturn(List.of(r));

        List<MeterReadingResponse> result =
                service.getByConnection("c1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getConnectionId()).isEqualTo("c1");
    }

    @Test
    void getPrevious_success_when_two_readings_exist() {

        MeterReading latest = MeterReading.builder()
                .id("m2")
                .connectionId("c1")
                .readingDate(LocalDate.now())
                .build();

        MeterReading previous = MeterReading.builder()
                .id("m1")
                .connectionId("c1")
                .readingDate(LocalDate.now().minusDays(1))
                .build();

        when(repository.findTop2ByConnectionIdOrderByReadingDateDesc("c1"))
                .thenReturn(List.of(latest, previous));

        MeterReadingResponse result =
                service.getPrevious("c1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("m1");
    }

    @Test
    void create_populates_all_fields() {

        ConnectionDTO connection = new ConnectionDTO();
        connection.setId("c1");
        connection.setStatus("ACTIVE");

        when(connectionClient.getConnection("c1"))
                .thenReturn(connection);

        when(repository.existsByConnectionIdAndReadingDate(any(), any()))
                .thenReturn(false);

        when(repository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        MeterReadingRequest request = MeterReadingRequest.builder()
                .connectionId("c1")
                .consumerId("cons1")
                .utilityId("u1")
                .readingValue(200)
                .readingDate(LocalDate.now())
                .build();

        MeterReadingResponse response =
                service.create(request);

        assertThat(response.getConnectionId()).isEqualTo("c1");
        assertThat(response.getConsumerId()).isEqualTo("cons1");
        assertThat(response.getUtilityId()).isEqualTo("u1");
    }

    @Test
    void getAllConnectionIdsWithReadings_empty() {

        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.getAllConnectionIdsWithReadings())
                .isEmpty();
    }

}
