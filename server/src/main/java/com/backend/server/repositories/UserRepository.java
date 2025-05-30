package com.backend.server.repositories;

import com.backend.server.entities.Patient;
import com.backend.server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail (String email);

    long deleteByEmail(String email);
}

