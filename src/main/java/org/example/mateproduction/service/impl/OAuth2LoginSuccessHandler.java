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

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .username(email)
                            .name(name)
                            .surname(surname)
                            .avatarUrl(picture)
                            .isVerified(true)
                            .status(UserStatus.ACTIVE)
                            .role(Role.USER)
                            .build();
                    return userRepository.save(newUser);
                });
        String token = jwtService.generateToken(new JwtUserDetails(user));

        response.sendRedirect(frontendBaseUrl + "/oauth-success?token=" + token);
    }

}
