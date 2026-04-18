package com.app.quantitymeasurement.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * TestSecurityConfig — Overrides SecurityConfig during integration tests.
 * Disables OAuth2 login requirement so TestRestTemplate can hit APIs directly.
 * This bean is only loaded when @Import(TestSecurityConfig.class) is used in a test.
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
