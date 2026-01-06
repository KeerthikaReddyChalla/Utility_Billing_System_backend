package com.chubb.utility.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.chubb.utility.models.Utility;

@DataMongoTest
class UtilityRepositoryTest {

    @Autowired
    private UtilityRepository repository;

    @Test
    void save_and_find_success() {
        Utility utility = repository.save(new Utility());

        assertThat(repository.findById(utility.getId())).isPresent();
    }
}
