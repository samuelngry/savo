package com.savo.backend.controller;

import com.savo.backend.model.User;
import com.savo.backend.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Profile", description = "User profile management API")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(
            summary = "Get user profile",
            description = "Get the authenticated user's profile information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = userDetails.getUsername();
            Optional<User> userOptional = userService.findById(userId);

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
