package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.model.*;
import com.app.quantitymeasurement.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for local user signup and login")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    @Operation(summary = "Register a new local user")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserEntity.Role.USER);

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate a local user")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> {
                    // In a real app, generate a JWT here. 
                    // For now, we return user details as a mock session.
                    Map<String, Object> session = new java.util.HashMap<>();
                    session.put("id", user.getId());
                    session.put("email", user.getEmail());
                    session.put("name", user.getName());
                    session.put("role", user.getRole().name());
                    session.put("message", "Login successful!");
                    return ResponseEntity.ok(session);
                })
                .orElse(ResponseEntity.status(401).body(Map.of("message", "Invalid email or password")));
    }
}
