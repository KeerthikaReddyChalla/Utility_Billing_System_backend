package com.chubb.consumer.repository;
import static org.assertj.core.api.Assertions.assertThat;

import com.chubb.consumer.models.Consumer;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
@DataMongoTest
class ConsumerRepositoryTest {

    @Autowired
    private ConsumerRepository repository;

    @Test
    void save_and_find_success() {
        Consumer c = repository.save(new Consumer());
        assertThat(repository.findById(c.getId())).isPresent();
    }
}

