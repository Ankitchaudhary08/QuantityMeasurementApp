package com.app.quantitymeasurement.config;

import com.app.quantitymeasurement.security.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig — UC18: Google OAuth2 Authentication + Role-Based Authorization.
 *
 * Public endpoints (no login required):
 *   - Swagger UI, OpenAPI docs, Actuator health
 *
 * Protected endpoints (must be logged in via Google):
 *   - /api/v1/quantities/** — all measurement operations
 *   - /api/v1/users/me      — current user profile
 *
 * Admin-only endpoints:
 *   - GET /api/v1/users     — list all users
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of("http://localhost:4200"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("*"));
        configuration.setAllowCredentials(true);
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Disable CSRF for REST API (stateless API clients don't use cookies)
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // ── Public endpoints ──────────────────────────────────────────
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/actuator/health",
                    "/actuator/info",
                    "/h2-console/**",
                    "/login",
                    "/oauth2/**",
                    "/api/v1/quantities/**",
                    "/api/v1/auth/**",
                    "/"
                ).permitAll()

                // ── Admin-only ─────────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")

                // ── All other /api/v1/** require authentication ────────────────
                .anyRequest().authenticated()
            )

            // ── Google OAuth2 Login ───────────────────────────────────────────
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .defaultSuccessUrl("/api/v1/users/me", true)
                .failureUrl("/login?error=true")
            )

            // ── Logout ────────────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )

            // ── Allow H2 console iframes (dev only) ──────────────────────────
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
