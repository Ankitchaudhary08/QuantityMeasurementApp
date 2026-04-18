package com.app.userservice.service;

import com.app.userservice.exception.UserAlreadyExistsException;
import com.app.userservice.exception.UserNotFoundException;
import com.app.userservice.model.*;
import com.app.userservice.repository.UserRepository;
import com.app.userservice.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UserService — Business logic for user registration, login, and profile management.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ----------------------------------------------------------------
    // Registration
    // ----------------------------------------------------------------

    /**
     * Register a new local user account.
     *
     * @param request signup details
     * @return created UserDTO (without password)
     */
    public UserDTO signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserEntity.Role.USER);

        UserEntity saved = userRepository.save(user);
        logger.info("New user registered: {}", saved.getEmail());
        return UserDTO.fromEntity(saved);
    }

    // ----------------------------------------------------------------
    // Login
    // ----------------------------------------------------------------

    /**
     * Authenticate user and issue a JWT token.
     *
     * @param request login credentials
     * @return map containing "token" and "user" keys
     */
    public Map<String, Object> login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("No account found for: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        logger.info("User logged in: {}", user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", UserDTO.fromEntity(user));
        return response;
    }

    // ----------------------------------------------------------------
    // Profile Management
    // ----------------------------------------------------------------

    /**
     * Get user profile by email.
     */
    public UserDTO getProfile(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        return UserDTO.fromEntity(user);
    }

    /**
     * Get all users (ADMIN only).
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID.
     */
    public UserDTO getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return UserDTO.fromEntity(user);
    }

    /**
     * Delete user by ID (ADMIN only).
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        logger.info("User deleted with id: {}", id);
    }
}
