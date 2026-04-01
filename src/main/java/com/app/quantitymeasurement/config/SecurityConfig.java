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

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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
