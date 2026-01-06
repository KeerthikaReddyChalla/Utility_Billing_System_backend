package com.chubb.meter.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.chubb.meter.dto.MeterReadingRequest;
import com.chubb.meter.dto.MeterReadingResponse;
import com.chubb.meter.exception.DuplicateReadingException;
import com.chubb.meter.exception.InvalidConnectionStateException;
import com.chubb.meter.exception.ResourceNotFoundException;
import com.chubb.meter.feign.ConnectionClient;
import com.chubb.meter.models.MeterReading;
import com.chubb.meter.repository.MeterReadingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeterReadingService {

    private final MeterReadingRepository repository;
    private final ConnectionClient connectionClient;

    public MeterReadingResponse create(MeterReadingRequest request) {

 
        var connection = connectionClient.getConnection(request.getConnectionId());


        if (!"ACTIVE".equals(connection.getStatus())) {
            throw new InvalidConnectionStateException(
                "Cannot add meter reading. Connection status is " + connection.getStatus()
            );
        }


   
        boolean exists = repository.existsByConnectionIdAndReadingDate(
                request.getConnectionId(),
                request.getReadingDate()
        );

        if (exists) {
            throw new DuplicateReadingException(
                    "Meter reading already exists for this connection and date"
            );
        }

 
        MeterReading reading = MeterReading.builder()
                .connectionId(request.getConnectionId())
                .consumerId(request.getConsumerId())   
                .utilityId(request.getUtilityId())     
                .readingValue(request.getReadingValue())
                .readingDate(request.getReadingDate())
                .createdAt(LocalDateTime.now())
                .build();

        return map(repository.save(reading));
    }

    public List<MeterReadingResponse> getByConnection(String connectionId) {
        return repository.findByConnectionId(connectionId)
                .stream()
                .map(this::map)
                .toList();
    }

    public MeterReadingResponse getLatest(String connectionId) {
        MeterReading reading = repository
                .findTopByConnectionIdOrderByReadingDateDesc(connectionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No meter readings found"));

        return map(reading);
    }
    private MeterReadingResponse map(MeterReading r) {
        return MeterReadingResponse.builder()
                .id(r.getId())
                .connectionId(r.getConnectionId())
                .consumerId(r.getConsumerId())   
                .utilityId(r.getUtilityId())    
                .readingValue(r.getReadingValue())
                .readingDate(r.getReadingDate())
                .createdAt(r.getCreatedAt())
                .build();
    }


    
    public MeterReadingResponse getPrevious(String connectionId) {

        List<MeterReading> readings =
                repository.findTop2ByConnectionIdOrderByReadingDateDesc(connectionId);

        // First billing cycle, no previous reading
        if (readings.size() < 2) {
            return null;
        }

        MeterReading previous = readings.get(1);

        return MeterReadingResponse.builder()
                .id(previous.getId())
                .connectionId(previous.getConnectionId())
                .consumerId(previous.getConsumerId())
                .utilityId(previous.getUtilityId())
                .readingValue(previous.getReadingValue())
                .readingDate(previous.getReadingDate())
                .createdAt(previous.getCreatedAt())
                .build();
    }
    
    public List<String> getAllConnectionIdsWithReadings() {
        return repository.findAll()
                .stream()
                .map(MeterReading::getConnectionId)
                .distinct()
                .toList();
    }



}
