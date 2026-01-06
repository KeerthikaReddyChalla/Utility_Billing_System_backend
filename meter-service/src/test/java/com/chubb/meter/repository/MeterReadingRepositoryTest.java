package com.chubb.meter.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.chubb.meter.models.MeterReading;

@DataMongoTest
class MeterReadingRepositoryTest {

    @Autowired
    private MeterReadingRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void existsByConnectionId_success() {

        repository.save(
                MeterReading.builder()
                        .connectionId("c1")
                        .readingDate(LocalDate.now())
                        .build()
        );

        assertThat(repository.existsByConnectionId("c1")).isTrue();
    }

    @Test
    void findTopByConnectionIdOrderByReadingDateDesc_success() {

        repository.save(
                MeterReading.builder()
                        .connectionId("c1")
                        .readingDate(LocalDate.now())
                        .build()
        );

        assertThat(
                repository.findTopByConnectionIdOrderByReadingDateDesc("c1")
        ).isPresent();
    }
}
