package com.chubb.utility.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.chubb.utility.models.Tariff;
import com.chubb.utility.models.TariffType;

@DataMongoTest
class TariffRepositoryTest {

    @Autowired
    private TariffRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void findByUtilityId_success() {

        repository.save(
                Tariff.builder()
                        .utilityId("u1")
                        .tariffType(TariffType.RESIDENTIAL_FLAT)
                        .active(true)
                        .build()
        );

        assertThat(repository.findByUtilityId("u1")).hasSize(1);
    }
}
