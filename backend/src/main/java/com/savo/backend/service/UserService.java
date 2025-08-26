package com.savo.backend.service;

import com.savo.backend.model.User;
import com.savo.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    // Traditional Login Method

    public User createLocalUser(String email, String username, String password,
                                String firstName, String lastName) {

        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with email " + email + " already exists");
        }

        if (username != null && userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username " + username + " is already taken");
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setProvider("local");
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // Authenticate local user

    public Optional<User> authenticateLocalUser(String emailOrUsername, String password) {
        // Try to find by email first, then username
        Optional<User> userOptional = userRepository.findByEmail(emailOrUsername);
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByUsername(emailOrUsername);
        }

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Only authenticate local users
            if ("local".equals(user.getProvider()) &&
                user.getPasswordHash() != null &&
                passwordEncoder.matches(password, user.getPasswordHash())) {

                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    // Common User Methods

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String getUserFullName(User user) {
        if (user.getLastName() != null && !user.getLastName().trim().isEmpty()) {
            return user.getFirstName() + " " + user.getLastName();
        }
        return user.getFirstName();
    }
}
