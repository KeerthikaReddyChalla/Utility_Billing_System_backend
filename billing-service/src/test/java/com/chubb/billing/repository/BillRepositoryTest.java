package com.chubb.billing.repository;

import com.chubb.billing.models.Bill;
import com.chubb.billing.models.BillStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class BillRepositoryTest {

    @Autowired
    private BillRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void cleanDb() {
        mongoTemplate.getDb().drop(); 
    }

    @Test
    void findByConsumerId_success() {

        Bill bill = Bill.builder()
                .consumerId("c1")
                .amount(100.0)
                .status(BillStatus.GENERATED)
                .build();

        repository.save(bill);

        List<Bill> result =
                repository.findByConsumerId("c1");

        assertThat(result).hasSize(1);
    }
}
