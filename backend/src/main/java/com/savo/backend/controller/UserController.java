package com.savo.backend.controller;

import com.savo.backend.model.User;
import com.savo.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
}
