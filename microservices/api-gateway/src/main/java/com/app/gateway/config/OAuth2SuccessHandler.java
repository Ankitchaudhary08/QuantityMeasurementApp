package com.app.gateway.config;

import com.app.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * OAuth2SuccessHandler — Handles successful Google authentication.
 * Generates a JWT token and redirects back to the Angular frontend with the token.
 */
@Component
public class OAuth2SuccessHandler extends RedirectServerAuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    private static final String FRONTEND_URL = "http://localhost:4200";

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        
        // Map Google user to our internal role (defaulting to USER)
        String token = jwtUtil.generateToken(email, "USER");

        // Redirect to frontend with token, name, and email as query params
        String name = oauthUser.getAttribute("name");
        String redirectUrl = FRONTEND_URL + "/?token=" + token + "&email=" + email + "&name=" + java.net.URLEncoder.encode(name != null ? name : "GoogleUser", java.nio.charset.StandardCharsets.UTF_8);
        
        org.springframework.http.server.reactive.ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(org.springframework.http.HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create(redirectUrl));
        
        return response.setComplete();
    }
}
