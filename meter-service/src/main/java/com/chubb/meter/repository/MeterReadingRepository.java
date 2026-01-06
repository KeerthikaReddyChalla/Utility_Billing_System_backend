package com.chubb.meter.repository;

import com.chubb.meter.models.MeterReading;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MeterReadingRepository extends MongoRepository<MeterReading, String> {

    boolean existsByConnectionIdAndReadingDate(String connectionId, LocalDate readingDate);

    List<MeterReading> findByConnectionId(String connectionId);

    Optional<MeterReading> findTopByConnectionIdOrderByReadingDateDesc(String connectionId);
    List<MeterReading> findTop2ByConnectionIdOrderByReadingDateDesc(String connectionId);
    boolean existsByConnectionId(String connectionId);
    
    List<MeterReading> findAll();
}
