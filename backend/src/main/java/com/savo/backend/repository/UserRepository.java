package com.savo.backend.repository;

import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // Auth use cases
    Optional<User> findByEmail(String email); // for login
    Optional<User> findByUsername(String username); // for traditional login
    Optional<User> findByProviderAndProviderId(String provider, String providerId); // for OAuth login

    // Validation use cases
    boolean existsByEmail(String email); // email already taken
    boolean existsByUsername(String username); // username taken

    // Search or filtering
    Optional<User> findById(String id); // redundant but explicit
}
