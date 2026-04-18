package com.app.userservice.controller;

import com.app.userservice.model.LoginRequest;
import com.app.userservice.model.SignupRequest;
import com.app.userservice.model.UserDTO;
import com.app.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AuthController — Public endpoints for user registration and login.
 * Base path: /api/v1/auth
 *
 * These routes are NOT protected by the JWT gateway filter.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User registration and login")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Creates a new local user account")
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody SignupRequest request) {
        logger.info("POST /auth/signup for email: {}", request.getEmail());
        UserDTO user = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns a JWT token")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        logger.info("POST /auth/login for email: {}", request.getEmail());
        Map<String, Object> response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
