package com.app.quantitymeasurement.config;

import com.app.quantitymeasurement.model.UserEntity;
import com.app.quantitymeasurement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@quanment.com")) {
            UserEntity admin = new UserEntity();
            admin.setName("Admin User");
            admin.setEmail("admin@quanment.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserEntity.Role.ADMIN);
            userRepository.save(admin);
        }
    }
}
