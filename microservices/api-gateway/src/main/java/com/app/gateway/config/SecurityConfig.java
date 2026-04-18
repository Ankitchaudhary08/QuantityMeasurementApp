package com.app.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SecurityConfig — WebFlux security config for the API Gateway.
 * JWT validation is handled by JwtAuthenticationFilter (GatewayFilter), not here.
 * This config only disables Spring Security's default form login / CSRF
 * and sets CORS for the Angular frontend.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private OAuth2SuccessHandler oauth2SuccessHandler;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/oauth2/**", "/login/**").permitAll()
                        .anyExchange().permitAll()   // JWT validation is done by GatewayFilter
                )
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(oauth2SuccessHandler)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://quantity-measurement-frontend-ankit.s3-website-us-east-1.amazonaws.com"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
