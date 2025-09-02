package com.savo.backend.service;

import com.savo.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User findOrCreateOAuthUser(String email, String firstName, String lastName, String provider, String providerId);

    User handleGoogleLogin(String email, String firstName, String lastName, String googleId);

    User createLocalUser(String email, String username, String password, String firstName, String lastName);

    Optional<User> authenticateLocalUser(String emailOrUsername, String password);

    User updateUserProfile(String id, String firstName, String lastName, String timezone, String currency);

    boolean changePassword(String id, String currentPassword, String newPassword);

    User verifyEmail(String id);

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    List<User> getAllUsers();

    String getUserFullName(User user);

    long getTotalUserCount();

    boolean isEmailTaken(String email);

    boolean isUsernameTaken(String username);

    boolean isOAuthUser(String id);

    User createSampleUser(String email, String firstName, String lastName);
}
