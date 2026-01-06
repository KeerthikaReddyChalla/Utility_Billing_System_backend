package com.chubb.utility.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chubb.utility.dto.CreateTariffRequest;
import com.chubb.utility.dto.TariffResponse;
import com.chubb.utility.dto.UpdateTariffRequest;
import com.chubb.utility.exception.ResourceNotFoundException;
import com.chubb.utility.models.Tariff;
import com.chubb.utility.repository.TariffRepository;
import com.chubb.utility.repository.UtilityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TariffService {
	private static final String TARIFF_NOT_FOUND = "Tariff not found";
    private final TariffRepository tariffRepository;
    private final UtilityRepository utilityRepository;

    public TariffResponse create(CreateTariffRequest request) {

        if (!utilityRepository.existsById(request.getUtilityId())) {
            throw new ResourceNotFoundException(
                    "Utility not found with id: " + request.getUtilityId());
        }

        Tariff tariff = Tariff.builder()
                .utilityId(request.getUtilityId())
                .tariffType(request.getTariffType())
                .ratePerUnit(request.getRatePerUnit())
                .fixedCharge(request.getFixedCharge())
                .active(true)
                .build();

        return map(tariffRepository.save(tariff));
    }

    public TariffResponse update(String tariffId, UpdateTariffRequest request) {

        Tariff tariff = tariffRepository.findById(tariffId)
                .orElseThrow(() -> new ResourceNotFoundException(TARIFF_NOT_FOUND));

        tariff.setRatePerUnit(request.getRatePerUnit());
        tariff.setFixedCharge(request.getFixedCharge());

        return map(tariffRepository.save(tariff));
    }

    public List<TariffResponse> getByUtility(String utilityId) {

        if (!utilityRepository.existsById(utilityId)) {
            throw new ResourceNotFoundException("Utility not found");
        }

        return tariffRepository.findByUtilityId(utilityId)
                .stream()
                .map(this::map)
                .toList();
    }

    public TariffResponse getById(String tariffId) {
        return tariffRepository.findById(tariffId)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException(TARIFF_NOT_FOUND));
    }

    public void delete(String tariffId) {
        Tariff tariff = tariffRepository.findById(tariffId)
                .orElseThrow(() -> new ResourceNotFoundException(TARIFF_NOT_FOUND));
        tariffRepository.delete(tariff);
    }

    private TariffResponse map(Tariff tariff) {
        return TariffResponse.builder()
                .id(tariff.getId())
                .utilityId(tariff.getUtilityId())
                .tariffType(tariff.getTariffType())
                .ratePerUnit(tariff.getRatePerUnit())
                .fixedCharge(tariff.getFixedCharge())
                .active(tariff.isActive())
                .build();
    }
    public List<TariffResponse> getAll() { 
    	return tariffRepository.findAll() .stream() .map(this::map) .toList(); 
    	}
    
    public Double getRateByUtilityId(String utilityId) {

        Tariff tariff = tariffRepository
                .findFirstByUtilityIdAndActiveTrue(utilityId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active tariff not found for utility: " + utilityId
                        )
                );

        return tariff.getRatePerUnit();
    }

    
}
