package com.savo.backend.controller;

import com.savo.backend.model.User;
import com.savo.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
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

            Map<String, Object> response = Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName() != null ? user.getLastName() : "",
                    "fullName", userService.getUserFullName(user),
                    "provider", user.getProvider(),
                    "emailVerified", user.getEmailVerified(),
                    "currency", user.getCurrency(),
                    "timezone", user.getTimezone(),
                    "message", "Google login successful"
            );

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

                Map<String, Object> response = Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "firstName", user.getFirstName(),
                        "fullName", userService.getUserFullName(user),
                        "username", user.getUsername() != null ? user.getUsername() : "",
                        "provider", user.getProvider(),
                        "emailVerified", user.getEmailVerified(),
                        "currency", user.getCurrency(),
                        "timezone", user.getTimezone(),
                        "message", "Login successful"
                );

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
}
