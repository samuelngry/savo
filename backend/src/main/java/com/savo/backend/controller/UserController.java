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

    @PutMapping("/profile")
    @Operation(
            summary = "Update user profile",
            description = "Update the authenticated user's profile information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<?> updateUserProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UpdateProfileRequest request) {
        try {
            String userId = userDetails.getUsername();

            User updatedUser = userService.updateUserProfile(
                    userId,
                    request.getFirstName(),
                    request.getLastName(),
                    request.getTimezone(),
                    request.getCurrency()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedUser.getId());
            response.put("email", updatedUser.getEmail());
            response.put("firstName", updatedUser.getFirstName());
            response.put("lastName", updatedUser.getLastName());
            response.put("fullName", userService.getUserFullName(updatedUser));
            response.put("currency", updatedUser.getCurrency());
            response.put("timezone", updatedUser.getTimezone());
            response.put("message", "Profile updated successfully");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user profile"));
        }
    }

    @PostMapping("/change-password")
    @Operation(
            summary = "Change user password",
            description = "Change the authenticated user's password (local accounts only)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid current password or OAuth user"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ChangePasswordRequest request) {
        try {
            String userId = userDetails.getUsername();

            if (request.getCurrentPassword()== null || request.getNewPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Current password and new password are required"));
            }

            if (userService.isOAuthUser(userId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot change password for OAuth users"));
            }

            boolean success = userService.changePassword(userId, request.currentPassword, request.newPassword);

            if (success) {
                return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Current password is incorrect"));
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

    public static class UpdateProfileRequest {
        private String firstName;
        private String lastName;
        private String currency;
        private String timezone;

        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
    }

    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        // Getters and setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
