package org.example.mateproduction.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtService;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.util.AuthProvider;
import org.example.mateproduction.util.Role;
import org.example.mateproduction.util.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("given_name");
        String surname = oauthUser.getAttribute("family_name");
        String picture = oauthUser.getAttribute("picture");

        // ✅ REVISED LOGIC
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            // --- CASE 1: USER ALREADY EXISTS ---
            // Update the existing user's details from the OAuth provider
            user = userOptional.get();
            user.setName(name);
            user.setSurname(surname);
            user.setAvatarUrl(picture);
            user.setAuthProvider(AuthProvider.GOOGLE); // Mark as a Google-linked account
            user.setIsVerified(true); // OAuth automatically verifies the email
        } else {
            // --- CASE 2: NEW USER ---
            // Create a brand new user record
            user = User.builder()
                    .email(email)
                    .username(email) // Default username to email
                    .name(name)
                    .surname(surname)
                    .avatarUrl(picture)
                    .isVerified(true)
                    .status(UserStatus.ACTIVE)
                    .role(Role.USER)
                    .authProvider(AuthProvider.GOOGLE) // Set the auth provider
                    .build();
        }

        // Save the updated or new user and generate a token
        userRepository.save(user);
        String token = jwtService.generateToken(new JwtUserDetails(user));

        // Correct the redirect URI if necessary. Your original property might be better.
        // This example assumes your frontend handles the redirect from /oauth-success
        String redirectUrl = frontendBaseUrl + "/oauth-success?token=" + token;

        response.sendRedirect(redirectUrl);
    }

}
