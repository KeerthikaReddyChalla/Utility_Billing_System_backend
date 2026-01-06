package com.chubb.consumer.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chubb.consumer.dto.*;
import com.chubb.consumer.exception.ResourceNotFoundException;
import com.chubb.consumer.feign.MeterClient;
import com.chubb.consumer.feign.UtilityClient;
import com.chubb.consumer.models.*;
import com.chubb.consumer.repository.*;

@ExtendWith(MockitoExtension.class)
class ConnectionServiceTest {

    @Mock private ConnectionRepository connectionRepo;
    @Mock private ConsumerRepository consumerRepo;
    @Mock private UtilityClient utilityClient;
    @Mock private ConnectionRequestRepository requestRepo;
    @Mock private MeterClient meterClient;

    @InjectMocks
    private ConnectionService service;

    @Test
    void getByConsumerId_success() {
        when(connectionRepo.findByConsumerId("c1"))
                .thenReturn(List.of(new Connection()));

        assertThat(service.getByConsumerId("c1")).hasSize(1);
    }

    @Test
    void getById_not_found() {
        when(connectionRepo.findById("x"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById("x"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_success() {
        ConnectionRequestDTO dto = new ConnectionRequestDTO(
                "c1", "u1", TariffType.RESIDENTIAL_FLAT
        );

        when(consumerRepo.existsById("c1")).thenReturn(true);
        when(connectionRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThat(service.create(dto)).isNotNull();
    }

    @Test
    void requestConnection_success() {
        ConnectionRequestDTO dto = new ConnectionRequestDTO(
                "c1", "u1", TariffType.RESIDENTIAL_FLAT
        );

        when(consumerRepo.existsById("c1")).thenReturn(true);

        service.requestConnection(dto);

        verify(requestRepo).save(any(ConnectionRequest.class));
    }

    @Test
    void getPendingRequests_success() {

        ConnectionRequest request = ConnectionRequest.builder()
                .id("r1")
                .consumerId("c1")
                .utilityId("u1")
                .tariffType(TariffType.RESIDENTIAL_FLAT)
                .status(RequestStatus.PENDING)  
                .requestedAt(LocalDateTime.now())
                .build();

        when(requestRepo.findByStatus(RequestStatus.PENDING))
                .thenReturn(List.of(request));

        List<ConnectionRequestResponseDTO> result =
                service.getPendingRequests();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
    }


    @Test
    void getAllForBillingOfficer_success() {

        Connection connection = Connection.builder()
                .id("conn1")
                .consumerId("c1")
                .utilityId("u1")
                .tariffType(TariffType.RESIDENTIAL_FLAT)
                .status(ConnectionStatus.ACTIVE) 
                .build();

        Consumer consumer = Consumer.builder()
                .id("c1")
                .fullName("John Doe")
                .build();

        when(connectionRepo.findAll())
                .thenReturn(List.of(connection));

        when(meterClient.hasMeterReading("conn1"))
                .thenReturn(false);

        when(consumerRepo.findById("c1"))
                .thenReturn(Optional.of(consumer));

        when(utilityClient.fetchUtility("u1"))
                .thenReturn(new UtilityMiniDTO("u1", "Electricity"));

        List<ConnectionBillingViewDTO> result =
                service.getAllForBillingOfficer();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getConsumerName()).isEqualTo("John Doe");
        assertThat(result.get(0).getUtilityName()).isEqualTo("Electricity");
    }
    
    @Test
    void create_consumerNotFound() {

        ConnectionRequestDTO dto =
                new ConnectionRequestDTO("c1", "u1", TariffType.RESIDENTIAL_FLAT);

        when(consumerRepo.existsById("c1"))
                .thenReturn(false);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateStatus_success() {

        Connection connection = Connection.builder()
                .id("conn1")
                .status(ConnectionStatus.PENDING)
                .build();

        when(connectionRepo.findById("conn1"))
                .thenReturn(Optional.of(connection));

        when(connectionRepo.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        ConnectionUpdateDTO dto = new ConnectionUpdateDTO();
        dto.setStatus(ConnectionStatus.ACTIVE);

        ConnectionResponseDTO result =
                service.updateStatus("conn1", dto);

        assertThat(result.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void processRequest_approved() {

        ConnectionRequest request = ConnectionRequest.builder()
                .id("r1")
                .consumerId("c1")
                .utilityId("u1")
                .tariffType(TariffType.RESIDENTIAL_FLAT)
                .status(RequestStatus.PENDING)
                .build();

        when(requestRepo.findById("r1"))
                .thenReturn(Optional.of(request));

        ConnectionRequestUpdateDTO dto =
                new ConnectionRequestUpdateDTO(RequestStatus.APPROVED);

        service.processRequest("r1", dto);

        verify(connectionRepo).save(any(Connection.class));
        verify(requestRepo).deleteById("r1");
    }

    @Test
    void processRequest_notFound() {

        when(requestRepo.findById("x"))
                .thenReturn(Optional.empty());

        ConnectionRequestUpdateDTO dto =
                new ConnectionRequestUpdateDTO(RequestStatus.APPROVED);

        assertThatThrownBy(() ->
                service.processRequest("x", dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllForBillingOfficer_consumerMissing() {

        Connection connection = Connection.builder()
                .id("conn1")
                .consumerId("c1")
                .utilityId("u1")
                .tariffType(TariffType.RESIDENTIAL_FLAT)
                .status(ConnectionStatus.ACTIVE)
                .build();

        when(connectionRepo.findAll())
                .thenReturn(List.of(connection));

        when(meterClient.hasMeterReading("conn1"))
                .thenReturn(false);

        when(consumerRepo.findById("c1"))
                .thenReturn(Optional.empty());

        when(utilityClient.fetchUtility("u1"))
                .thenReturn(new UtilityMiniDTO("u1", "Electricity"));

        List<ConnectionBillingViewDTO> result =
                service.getAllForBillingOfficer();

        assertThat(result.get(0).getConsumerName())
                .isEqualTo("Unknown");
    }

    @Test
    void getAllConnections_success() {

        when(connectionRepo.findAll())
                .thenReturn(List.of(new Connection()));

        assertThat(service.getAllConnections()).hasSize(1);
    }


}
