package com.chubb.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.chubb.auth.models.Role;
import com.chubb.auth.models.User;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    List<User> findByRoleAndActive(Role role, boolean active);
}
