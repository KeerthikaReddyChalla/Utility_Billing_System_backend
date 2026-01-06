package com.chubb.consumer.service;

import com.chubb.consumer.dto.*;
import com.chubb.consumer.exception.ResourceNotFoundException;
import com.chubb.consumer.feign.MeterClient;
import com.chubb.consumer.feign.UtilityClient;
import com.chubb.consumer.models.*;
import com.chubb.consumer.repository.ConnectionRepository;
import com.chubb.consumer.repository.ConnectionRequestRepository;
import com.chubb.consumer.repository.ConsumerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository connectionRepo;
    private final ConsumerRepository consumerRepo;
    private final UtilityClient utilityClient;
    private final ConnectionRequestRepository requestRepo;
    private final MeterClient meterClient;

    // ===============================
    // CONSUMER VIEWS
    // ===============================

    public List<ConnectionResponseDTO> getByConsumerId(String consumerId) {
        return connectionRepo.findByConsumerId(consumerId)
                .stream()
                .map(this::map)
                .toList();
    }

    public ConnectionResponseDTO getById(String connectionId) {
        Connection connection = connectionRepo.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found"));
        return map(connection);
    }

    public ConnectionResponseDTO updateStatus(String connectionId, ConnectionUpdateDTO dto) {

        Connection connection = connectionRepo.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found"));

        connection.setStatus(dto.getStatus());

        return map(connectionRepo.save(connection));
    }

 

    public ConnectionResponseDTO create(ConnectionRequestDTO dto) {

        if (!consumerRepo.existsById(dto.getConsumerId())) {
            throw new ResourceNotFoundException("Consumer not found");
        }

        utilityClient.getUtilityById(dto.getUtilityId());

        Connection connection = Connection.builder()
                .consumerId(dto.getConsumerId())
                .utilityId(dto.getUtilityId())
                .tariffType(dto.getTariffType())
                .status(ConnectionStatus.ACTIVE)
                .build();

        Connection saved = connectionRepo.save(connection);

      

        return map(saved);
    }


 

    public void requestConnection(ConnectionRequestDTO dto) {

        if (!consumerRepo.existsById(dto.getConsumerId())) {
            throw new ResourceNotFoundException("Consumer not found");
        }

        utilityClient.getUtilityById(dto.getUtilityId());

        ConnectionRequest request = ConnectionRequest.builder()
                .consumerId(dto.getConsumerId())
                .utilityId(dto.getUtilityId())
                .tariffType(dto.getTariffType())
                .status(RequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        requestRepo.save(request);
    }



    public List<ConnectionRequestResponseDTO> getPendingRequests() {
        return requestRepo.findByStatus(RequestStatus.PENDING)
                .stream()
                .map(this::mapRequestToResponse)
                .toList();
    }

    public void processRequest(String requestId, ConnectionRequestUpdateDTO dto) {

        ConnectionRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (dto.getStatus() == RequestStatus.APPROVED) {

            Connection connection = Connection.builder()
                    .consumerId(request.getConsumerId())
                    .utilityId(request.getUtilityId())
                    .tariffType(request.getTariffType())
                    .status(ConnectionStatus.ACTIVE)
                    .build();

            connectionRepo.save(connection);

           

            requestRepo.deleteById(requestId);
        }

    }



    public List<ConnectionBillingViewDTO> getAllForBillingOfficer() {

        return connectionRepo.findAll()
                .stream()
                .filter(c -> c.getStatus() == ConnectionStatus.ACTIVE)
                .filter(c -> !meterClient.hasMeterReading(c.getId())) 
                .map(connection -> {

                    Consumer consumer = consumerRepo
                            .findById(connection.getConsumerId())
                            .orElse(null); // ❗ do NOT throw

                    UtilityMiniDTO utility =
                            utilityClient.fetchUtility(connection.getUtilityId());

                    return ConnectionBillingViewDTO.builder()
                            .connectionId(connection.getId())
                            .consumerId(connection.getConsumerId())
                            .consumerName(
                                    consumer != null ? consumer.getFullName() : "Unknown"
                            )
                            .utilityId(utility.getId())
                            .utilityName(utility.getName())
                            .tariffType(connection.getTariffType())
                            .status(connection.getStatus().name())
                            .build();
                })
                .toList();
    }


  
    private ConnectionResponseDTO map(Connection c) {
        return ConnectionResponseDTO.builder()
                .id(c.getId())
                .consumerId(c.getConsumerId())
                .utilityId(c.getUtilityId())
                .tariffType(c.getTariffType())
                .status(
                        c.getStatus() != null
                                ? c.getStatus().name()
                                : ConnectionStatus.PENDING.name()
                )
                .build();
    }

    private ConnectionRequestResponseDTO mapRequestToResponse(ConnectionRequest request) {
        return ConnectionRequestResponseDTO.builder()
                .id(request.getId())
                .consumerId(request.getConsumerId())
                .utilityId(request.getUtilityId())
                .tariffType(request.getTariffType())
                .status(request.getStatus().name())
                .requestedAt(request.getRequestedAt())
                .build();
    }
    public List<ConnectionResponseDTO> getAllConnections() {
        return connectionRepo.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

}
