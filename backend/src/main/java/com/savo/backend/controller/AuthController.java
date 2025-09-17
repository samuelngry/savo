package com.savo.backend.controller;

import com.savo.backend.model.User;
import com.savo.backend.service.JwtService;
import com.savo.backend.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
@Tag(name = "Authentication", description = "User Authentication API")
public class AuthController {

    private final UserServiceImpl userService;
    private final JwtService jwtService;

    public AuthController(UserServiceImpl userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Create a new user with email and password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Registration successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid request or email already exists")
            }
    )
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            if (request.getEmail() == null || request.getPassword() == null || request.getFirstName() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email, password, and firstName are required"));
            }

            if (userService.isEmailTaken(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email already exists"));
            }

            User user = userService.createLocalUser(
                    request.getEmail(),
                    request.getUsername(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName()
            );

            Map<String, Object> response = Map.of(
                    "user", createUserResponse(user),
                    "message", "User registered successfully. Please verify your email."
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticate user with email/username and password, returns JWT token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            if (request.getEmailOrUsername() == null || request.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email/username and password are required"));
            }

            Optional<User> userOptional = userService.authenticateLocalUser(
                    request.getEmailOrUsername(),
                    request.getPassword()
            );

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String token = jwtService.generateToken(user.getId(), user.getEmail());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", createUserResponse(user));
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
}
