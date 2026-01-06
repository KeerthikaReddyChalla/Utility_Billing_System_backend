package com.chubb.utility.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.chubb.utility.models.Tariff;
import com.chubb.utility.models.TariffType;

public interface TariffRepository extends MongoRepository<Tariff, String> {

    List<Tariff> findByUtilityId(String utilityId);

    Optional<Tariff> findByUtilityIdAndTariffTypeAndActiveTrue(
            String utilityId, TariffType tariffType);
    Optional<Tariff> findFirstByUtilityIdAndActiveTrue(String utilityId);
}
