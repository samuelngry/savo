package com.savo.backend.service;

import com.savo.backend.model.User;
import com.savo.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // OAuth Method

    public User findOrCreateOAuthUser(String email, String firstName, String lastName,
                                      String provider, String providerId) {

        // Try to find existing user by provider and providerId
        Optional<User> existingOAuthUser = userRepository.findByProviderAndProviderId(provider, providerId);
        if (existingOAuthUser.isPresent()) {
            return existingOAuthUser.get();
        }

        // If not found, try to find by email (user might have registered normally first)
        Optional<User> existingEmailUser = userRepository.findByEmail(email);
        if (existingEmailUser.isPresent()) {
            User user = existingEmailUser.get();
            // Link this OAuth account to existing user
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setEmailVerified(true);
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }

        // Create new OAuth user
        User newUser = new User();
        newUser.setId(UUID.randomUUID().toString());
        newUser.setEmail(email);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setProvider(provider);
        newUser.setProviderId(providerId);
        newUser.setEmailVerified(true);
        newUser.setPasswordHash(null);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(newUser);
    }

    public User handleGoogleLogin(String email, String firstName, String lastName, String googleId) {
        return findOrCreateOAuthUser(email, firstName, lastName, "google", googleId);
    }
}
