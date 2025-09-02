package com.savo.backend.controller;

import com.savo.backend.model.User;
import com.savo.backend.service.impl.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    // OAuth Endpoints

    @PostMapping("/oauth/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");
            String googleId = request.get("googleId");

            if (email == null || firstName == null || lastName == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email, firstName, and googleId are required"));
            }

            User user = userService.handleGoogleLogin(email, firstName, lastName, googleId);

            Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("email", user.getEmail());
                response.put("firstName", user.getFirstName());
                response.put("lastName", user.getLastName() != null ? user.getLastName() : "");
                response.put("fullName", userService.getUserFullName(user));
                response.put("provider", user.getProvider());
                response.put("emailVerified", user.getEmailVerified());
                response.put("currency", user.getCurrency());
                response.put("timezone", user.getTimezone());
                response.put("message", "Google login successful");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Google login failed: " + e.getMessage()));
        }
    }

    // Traditional Auth Endpoints

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String username = request.get("username"); // optional
            String password = request.get("password");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");

            if (email == null || password == null || firstName == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email, password, and firstName are required"));
            }

            if (userService.isEmailTaken(email)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email already exists"));
            }

            User user = userService.createLocalUser(email, username, password, firstName, lastName);

            Map<String, Object> response = Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "username", user.getUsername() != null ? user.getUsername() : "",
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName(),
                    "emailVerified", user.getEmailVerified(),
                    "message", "User registered successfully. Please verify your email."
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String emailOrUsername = request.get("emailOrUsername");
            String password = request.get("password");

            if (emailOrUsername == null || password == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email/username and password are required"));
            }

            Optional<User> userOptional = userService.authenticateLocalUser(emailOrUsername, password);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("email", user.getEmail());
                response.put("firstName", user.getFirstName());
                response.put("lastName", user.getLastName() != null ? user.getLastName() : "");
                response.put("fullName", userService.getUserFullName(user));
                response.put("username", user.getUsername() != null ? user.getUsername() : "");
                response.put("provider", user.getProvider());
                response.put("emailVerified", user.getEmailVerified());
                response.put("currency", user.getCurrency());
                response.put("timezone", user.getTimezone());
                response.put("message", "Login successful");

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid username or password"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    // User Profile Endpoints

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable String id) {
        try {
            Optional<User> userOptional = userService.findById(id);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                Map<String, Object> profile = new HashMap<>();
                profile.put("id", user.getId());
                profile.put("email", user.getEmail());
                profile.put("firstName", user.getFirstName());
                profile.put("lastName", user.getLastName() != null ? user.getLastName() : "");
                profile.put("fullName", userService.getUserFullName(user));
                profile.put("username", user.getUsername() != null ? user.getUsername() : "");
                profile.put("provider", user.getProvider());
                profile.put("emailVerified", user.getEmailVerified());
                profile.put("currency", user.getCurrency());
                profile.put("timezone", user.getTimezone());
                profile.put("createdAt", user.getCreatedAt());
                profile.put("isOAuthUser", userService.isOAuthUser(user.getId()));

                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.notFound().build();
            }

        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("{id}/profile")
    public ResponseEntity<?> updateUserProfile(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");
            String currency = request.get("currency");
            String timezone = request.get("timezone");

            User updatedUser = userService.updateUserProfile(id, firstName, lastName, timezone, currency);

            Map<String, Object> response = Map.of(
                    "id", updatedUser.getId(),
                    "email", updatedUser.getEmail(),
                    "firstName", updatedUser.getFirstName(),
                    "lastName", updatedUser.getLastName(),
                    "fullName", userService.getUserFullName(updatedUser),
                    "currency", updatedUser.getCurrency(),
                    "timezone", updatedUser.getTimezone(),
                    "message", "Profile updated successfully"
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user profile"));
        }
    }

    @PostMapping("{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");

            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Current password and new password are required"));
            }

            boolean success = userService.changePassword(id, currentPassword, newPassword);

            if (success) {
                return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Password changed failed"));
            }

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to change password"));
        }
    }

    @PostMapping("{id}/verify-email")
    public ResponseEntity<?> verifyEmail(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            User user = userService.verifyEmail(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Email verified successfully",
                    "emailVerified", user.getEmailVerified()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
