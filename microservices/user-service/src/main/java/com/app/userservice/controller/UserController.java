package com.app.userservice.controller;

import com.app.userservice.model.UserDTO;
import com.app.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserController — Protected endpoints for user profile and admin operations.
 * Base path: /api/v1/users
 *
 * Requests reach here only after JWT is validated by the API Gateway.
 * The gateway injects X-Auth-User-Email and X-Auth-User-Role headers.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "User profile and admin endpoints")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns the profile of the authenticated user")
    public ResponseEntity<UserDTO> getMyProfile(@RequestHeader("X-Auth-User-Email") String email) {
        logger.info("GET /users/me for: {}", email);
        return ResponseEntity.ok(userService.getProfile(email));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Admin: get any user by their ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id,
                                               @RequestHeader("X-Auth-User-Role") String role) {
        logger.info("GET /users/{} by role: {}", id, role);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    @Operation(summary = "List all users", description = "Admin only: returns all registered users")
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestHeader("X-Auth-User-Role") String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }
        logger.info("GET /users (admin)");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Admin only: delete a user account")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                           @RequestHeader("X-Auth-User-Role") String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }
        logger.info("DELETE /users/{} (admin)", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
