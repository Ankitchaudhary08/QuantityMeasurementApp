package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.model.UserEntity;
import com.app.quantitymeasurement.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * UserController — REST endpoints for user profile and management.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Endpoints for authenticated user profile and admin user management")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * GET /api/v1/users/me
     * Returns the currently authenticated user's profile.
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns the Google OAuth2 profile of the currently logged-in user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        String googleId = principal.getAttribute("sub");
        return userRepository.findByGoogleId(googleId)
                .map(user -> {
                    Map<String, Object> profile = new java.util.HashMap<>();
                    profile.put("id",          user.getId());
                    profile.put("googleId",    user.getGoogleId());
                    profile.put("email",       user.getEmail());
                    profile.put("name",        user.getName());
                    profile.put("pictureUrl",  user.getPictureUrl() != null ? user.getPictureUrl() : "");
                    profile.put("role",        user.getRole().name());
                    profile.put("createdAt",   user.getCreatedAt().toString());
                    profile.put("lastLoginAt", user.getLastLoginAt().toString());
                    return ResponseEntity.ok(profile);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/users (ADMIN only — secured in SecurityConfig)
     * Returns all registered users.
     */
    @GetMapping
    @Operation(summary = "List all users (Admin)", description = "Returns a list of all registered users — Admin access only")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
