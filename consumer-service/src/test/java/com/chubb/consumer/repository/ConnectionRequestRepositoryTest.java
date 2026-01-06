package com.chubb.consumer.repository;

import com.chubb.consumer.models.ConnectionRequest;
import com.chubb.consumer.models.RequestStatus;
import com.chubb.consumer.models.TariffType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ConnectionRequestRepositoryTest {

    @Autowired
    private ConnectionRequestRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void cleanDb() {
        mongoTemplate.getDb().drop(); 
    }

    @Test
    void findByStatus_success() {

        ConnectionRequest request = ConnectionRequest.builder()
                .consumerId("c1")
                .utilityId("u1")
                .tariffType(TariffType.RESIDENTIAL_FLAT)
                .status(RequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        repository.save(request);

        List<ConnectionRequest> result =
                repository.findByStatus(RequestStatus.PENDING);

        assertThat(result).hasSize(1);
    }
}
