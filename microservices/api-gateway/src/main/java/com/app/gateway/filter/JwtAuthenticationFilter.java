package com.app.gateway.filter;

import com.app.gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * JwtAuthenticationFilter — Gateway filter that validates JWT tokens
 * before forwarding requests to downstream microservices.
 *
 * Applied to all routes except: /api/v1/auth/login, /api/v1/auth/signup
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtUtil jwtUtil;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            logger.debug("Gateway filter processing path: {}", path);

            // Skip JWT validation for public endpoints
            if (isPublicPath(path)) {
                logger.debug("Public path, skipping JWT validation: {}", path);
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                logger.warn("Missing or invalid Authorization header for path: {}", path);
                return unauthorized(exchange, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(BEARER_PREFIX.length());

            try {
                if (!jwtUtil.validateToken(token)) {
                    logger.warn("Invalid JWT token for path: {}", path);
                    return unauthorized(exchange, "Invalid or expired JWT token");
                }

                String email = jwtUtil.extractEmail(token);
                String role = jwtUtil.extractRole(token);

                logger.debug("JWT validated for user: {}, role: {}", email, role);

                // Forward user info in headers to downstream services
                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(r -> r
                                .header("X-Auth-User-Email", email)
                                .header("X-Auth-User-Role", role))
                        .build();

                return chain.filter(mutatedExchange);
            } catch (Exception e) {
                logger.error("JWT validation error: {}", e.getMessage());
                return unauthorized(exchange, "JWT validation failed: " + e.getMessage());
            }
        };
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/signup")
                || path.equals("/actuator/health");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        byte[] bytes = ("{\"error\":\"" + message + "\"}").getBytes();
        var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    public static class Config {
        // Configuration properties (extendable)
    }
}
