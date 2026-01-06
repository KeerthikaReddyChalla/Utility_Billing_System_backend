package com.chubb.consumer.repository;

import com.chubb.consumer.models.Connection;
import com.chubb.consumer.models.ConnectionStatus;
import com.chubb.consumer.models.TariffType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ConnectionRepositoryTest {

    @Autowired
    private ConnectionRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void cleanDb() {
        mongoTemplate.getDb().drop(); 
    }

    @Test
    void findByConsumerId_success() {

        Connection c = Connection.builder()
                .consumerId("c1")
                .utilityId("u1")
                .tariffType(TariffType.RESIDENTIAL_FLAT)
                .status(ConnectionStatus.ACTIVE)
                .build();

        repository.save(c);

        List<Connection> result =
                repository.findByConsumerId("c1");

        assertThat(result).hasSize(1);
    }
}
