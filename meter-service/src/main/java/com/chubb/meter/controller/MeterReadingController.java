package com.chubb.meter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.chubb.meter.dto.MeterReadingRequest;
import com.chubb.meter.dto.MeterReadingResponse;
import com.chubb.meter.repository.MeterReadingRepository;
import com.chubb.meter.service.MeterReadingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/meter-readings")
@RequiredArgsConstructor
public class MeterReadingController {

    private final MeterReadingService service;
    private final MeterReadingRepository repository;
    /**
     * Create a meter reading for a connection
     * Role: BILLING_OFFICER
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('BILLING_OFFICER')")
    public MeterReadingResponse create(
            @Valid @RequestBody MeterReadingRequest request) {
        return service.create(request);
    }

    /**
     * Get all meter readings for a connection
     * Role: ADMIN, BILLING_OFFICER
     */
    @GetMapping("/{connectionId}")
    @PreAuthorize("hasAnyRole('BILLING_OFFICER','ADMIN')")
    public List<MeterReadingResponse> getByConnection(
            @PathVariable("connectionId") String connectionId) {
        return service.getByConnection(connectionId);
    }

    /**
     * Get latest meter reading for a connection
     * Role: ADMIN, BILLING_OFFICER
     */
    @GetMapping("/latest/{connectionId}")
    @PreAuthorize("hasAnyRole('BILLING_OFFICER','ADMIN')")
    public MeterReadingResponse getLatest(
            @PathVariable("connectionId") String connectionId) {
        return service.getLatest(connectionId);
    }
    @GetMapping("/previous/{connectionId}")
    @PreAuthorize("hasAnyRole('ADMIN','BILLING_OFFICER')")
    public ResponseEntity<MeterReadingResponse> getPrevious(
            @PathVariable("connectionId") String connectionId) {

        MeterReadingResponse response = service.getPrevious(connectionId);

        if (response == null) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(response); 
    }
    @GetMapping("/internal/exists/{connectionId}")
    @PreAuthorize("hasRole('BILLING_OFFICER')")
    public boolean hasReading(@PathVariable("connectionId") String connectionId) {
        return repository.existsByConnectionId(connectionId);
    }
    
    @GetMapping("/internal/connections-with-readings")
    @PreAuthorize("hasRole('BILLING_OFFICER')")
    public List<String> getConnectionsWithReadings() {
        return service.getAllConnectionIdsWithReadings();
    }


}
