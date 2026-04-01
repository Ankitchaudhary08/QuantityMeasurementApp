package com.app.quantitymeasurement.security;

import com.app.quantitymeasurement.model.UserEntity;
import com.app.quantitymeasurement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * CustomOAuth2UserService — Processes Google OAuth2 login.
 * After Google authentication succeeds, this service:
 *  1. Extracts the user's profile from the Google token.
 *  2. Creates a new UserEntity in the DB if it's a first login.
 *  3. Updates the lastLoginAt timestamp for returning users.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String googleId = oAuth2User.getAttribute("sub");
        String email    = oAuth2User.getAttribute("email");
        String name     = oAuth2User.getAttribute("name");
        String picture  = oAuth2User.getAttribute("picture");

        Optional<UserEntity> existingUser = userRepository.findByGoogleId(googleId);

        if (existingUser.isPresent()) {
            // Returning user — update last login timestamp
            UserEntity user = existingUser.get();
            user.setName(name);         // name can change in Google profile
            user.setPictureUrl(picture);
            userRepository.save(user);
            logger.info("Returning user logged in: {} ({})", email, googleId);
        } else {
            // New user — create a record
            UserEntity newUser = new UserEntity();
            newUser.setGoogleId(googleId);
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPictureUrl(picture);
            newUser.setRole(UserEntity.Role.USER);
            userRepository.save(newUser);
            logger.info("New user registered via Google OAuth: {} ({})", email, googleId);
        }

        return oAuth2User;
    }
}
