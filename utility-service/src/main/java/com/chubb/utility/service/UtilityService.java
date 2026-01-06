package com.chubb.utility.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chubb.utility.dto.UtilityRequest;
import com.chubb.utility.dto.UtilityResponse;
import com.chubb.utility.exception.ResourceConflictException;
import com.chubb.utility.exception.ResourceNotFoundException;
import com.chubb.utility.models.Utility;
import com.chubb.utility.repository.TariffRepository;
import com.chubb.utility.repository.UtilityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UtilityService {

    private final UtilityRepository repository;
    private final TariffRepository tariffRepo;

    public UtilityResponse create(UtilityRequest req) {
        Utility utility = Utility.builder()
                .name(req.getName())
                .description(req.getDescription())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        return map(repository.save(utility));
    }

    public List<UtilityResponse> getAll() {
        return repository.findAll().stream().map(this::map).toList();
    }

    public UtilityResponse getById(String id) {
        Utility utility = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utility not found"));
        return map(utility);
    }

    public UtilityResponse update(String id, UtilityRequest req) {
        Utility utility = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utility not found"));

        utility.setName(req.getName());
        utility.setDescription(req.getDescription());

        return map(repository.save(utility));
    }

    private UtilityResponse map(Utility u) {
        return UtilityResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .description(u.getDescription())
                .active(u.isActive())
                .build();
    }
    public void delete(String utilityId) {

        if (!repository.existsById(utilityId)) {
            throw new ResourceNotFoundException(
                    "Utility not found with id: " + utilityId);
        }


        if (!tariffRepo.findByUtilityId(utilityId).isEmpty()) {
            throw new ResourceConflictException(
                    "Cannot delete utility. Tariffs exist for this utility.");
        }

        repository.deleteById(utilityId);
    }
}
