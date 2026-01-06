package com.chubb.utility.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.chubb.utility.models.Utility;

public interface UtilityRepository extends MongoRepository<Utility, String> {
}
