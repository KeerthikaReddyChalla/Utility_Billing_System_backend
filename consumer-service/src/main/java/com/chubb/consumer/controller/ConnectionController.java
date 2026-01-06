package com.chubb.consumer.controller;

import com.chubb.consumer.dto.*;
import com.chubb.consumer.service.ConnectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/connections")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConnectionResponseDTO> create(
            @Valid @RequestBody ConnectionRequestDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{consumerId}")
    @PreAuthorize("hasAnyRole('ADMIN','CONSUMER')")
    public ResponseEntity<List<ConnectionResponseDTO>> getByConsumer(
            @PathVariable("consumerId") String consumerId) {
        return ResponseEntity.ok(service.getByConsumerId(consumerId));
    }

    @PutMapping("/{connectionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConnectionResponseDTO> updateStatus(
            @PathVariable("connectionId") String connectionId,
            @RequestBody ConnectionUpdateDTO dto) {
        return ResponseEntity.ok(service.updateStatus(connectionId, dto));
    }
    @GetMapping("/internal/{connectionId}")
    @PreAuthorize("hasAnyRole('ADMIN','BILLING_OFFICER')")
    public ConnectionResponseDTO getInternal(
            @PathVariable("connectionId") String connectionId) {
        return service.getById(connectionId);
    }
    
    
    @PostMapping("/request")
    @PreAuthorize("hasRole('CONSUMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void requestConnection(
            @Valid @RequestBody ConnectionRequestDTO dto) {

        service.requestConnection(dto);
    }
    
    @GetMapping("/requests/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConnectionRequestResponseDTO>> getPendingRequests() {
        return ResponseEntity.ok(service.getPendingRequests());
    }
    
    @PutMapping("/requests/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveOrRejectRequest(
            @PathVariable("requestId") String requestId,
            @RequestBody ConnectionRequestUpdateDTO dto) {

        service.processRequest(requestId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/internal/billing-view")
    @PreAuthorize("hasRole('BILLING_OFFICER')")
    public List<ConnectionBillingViewDTO> getForBillingOfficer() {
        return service.getAllForBillingOfficer();
    }

    @GetMapping("/internal/all")
    @PreAuthorize("hasRole('BILLING_OFFICER')")
    public List<ConnectionResponseDTO> getAllConnectionsInternal() {
        return service.getAllConnections();
    }




}
