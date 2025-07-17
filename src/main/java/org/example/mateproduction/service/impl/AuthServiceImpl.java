package org.example.mateproduction.service.impl;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtService;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.config.RabbitMqConfig;
import org.example.mateproduction.dto.request.LoginRequest;
import org.example.mateproduction.dto.request.RegisterRequest;
import org.example.mateproduction.dto.request.ResetPasswordRequest;
import org.example.mateproduction.dto.request.Verify2FARequest;
import org.example.mateproduction.dto.response.LoginResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.Token;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.*;
import org.example.mateproduction.helpers.Auditable;
import org.example.mateproduction.repository.TokenRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.AuthService;
import org.example.mateproduction.service.EmailService;
import org.example.mateproduction.util.Role;
import org.example.mateproduction.util.TokenType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CloudinaryService cloudinaryService;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqConfig rabbitMqConfig;
    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${app.frontend.verify-email-path}")
    private String verifyEmailPath;

    @Value("${app.frontend.reset-password-path}")
    private String resetPasswordPath;


    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) throws AlreadyExistException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AlreadyExistException("User with this email already exists");
        }



        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.GUEST)
                .name(request.getName())
                .surname(request.getSurname())
                .username(request.getUsername())
                .phone(request.getPhone())
                .isVerified(false)
                .avatarUrl(request.getAvatar() != null && !request.getAvatar().isEmpty()
                        ? cloudinaryService.upload(request.getAvatar())
                        : null)
                .build();

        userRepository.save(user);


        String token = generateAndSaveToken(user, TokenType.EMAIL_VERIFICATION);

        // --- [FIXED] CONSTRUCT THE VERIFICATION LINK TO POINT TO THE FRONTEND ---
        // This link now correctly points to your React application's verification page.
        String verificationLink = frontendBaseUrl + verifyEmailPath + "?token=" + token;
        // --- [END FIXED] ---

        String emailBody = buildEmail("Verify Your Account", "Please click the link below to verify your account:", verificationLink, "Verify Account");
        emailService.sendEmail(user.getEmail(), "Account Verification", emailBody);
//        rabbitTemplate.convertAndSend("notification_exchange", "notification_routing_key", emailBody);
        return buildUserResponse(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) throws NotFoundException {
        // 1. Authenticate username and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Fetch the user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with this email"));

        // 3. Check if 2FA is enabled
        if (user.getIsTwoFaEnabled() != null && user.getIsTwoFaEnabled()) {
            // If yes, return a response indicating that 2FA is required.
            // Do NOT return the token/user details yet.
            return LoginResponse.builder()
                    .twoFactorEnabled(true)
                    .build();
        }

        // 4. If 2FA is not enabled, build the full response with the token
        UserResponse userResponse = buildUserResponse(user);
        return LoginResponse.builder()
                .twoFactorEnabled(false)
                .user(userResponse)
                .build();
    }

    @Override
    public UserResponse verify2FA(Verify2FARequest request) throws NotFoundException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with this email"));

        // Verify the provided code
        if (request.getCode() == null || !verifyCode(user.getTwoFaSecret(), request.getCode())) {
            throw new RuntimeException("Invalid or missing 2FA code");
        }

        // If the code is valid, now we can return the final response with the JWT token
        return buildUserResponse(user);
    }

    public void enableTwoFa(User user, String secret) {
        user.setIsTwoFaEnabled(true);
        user.setTwoFaSecret(secret);
        userRepository.save(user);
    }

    public void disableTwoFa(User user) {
        user.setIsTwoFaEnabled(false);
        user.setTwoFaSecret(null);
        userRepository.save(user);
    }

    private UserResponse buildUserResponse(User user) {
        String token = jwtService.generateToken(new JwtUserDetails(user));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isVerified(user.getIsVerified())
                .avatarUrl(user.getAvatarUrl())
                .token(token)
                .build();
    }

    @Override
    @Transactional
    @Auditable(action = "VERIFY_ACCOUNT")
    public void verifyAccount(String token) {
        Token verificationToken = findAndValidateToken(token, TokenType.EMAIL_VERIFICATION);
        User user = verificationToken.getUser();
        user.setIsVerified(true);
        userRepository.save(user);

        verificationToken.setConfirmedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) throws NotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " not found."));

        if (!user.getIsVerified()){
            throw new UnverifiedUserException("Cannot reset password for an unverified account. Please verify your account first.");
        }

        String token = generateAndSaveToken(user, TokenType.PASSWORD_RESET);
        // --- [FIXED] CONSTRUCT THE RESET LINK TO POINT TO THE FRONTEND ---
        String resetLink = frontendBaseUrl + resetPasswordPath + "?token=" + token;
        // --- [END FIXED] ---

        String emailBody = buildEmail("Reset Your Password", "Please click the link below to reset your password:", resetLink, "Reset Password");
        emailService.sendEmail(user.getEmail(), "Password Reset Request", emailBody);
    }

    @Override
    @Transactional
    @Auditable(action = "RESET_PASSWORD")
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordsNotMatchException("Passwords do not match.");
        }

        Token resetToken = findAndValidateToken(request.getToken(), TokenType.PASSWORD_RESET);
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setConfirmedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);
    }

    private String generateAndSaveToken(User user, TokenType tokenType) {
        String tokenString = UUID.randomUUID().toString();
        Token token = Token.builder()
                .token(tokenString)
                .tokenType(tokenType)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(token);
        return tokenString;
    }

    private Token findAndValidateToken(String tokenString, TokenType expectedType) {
        Token token = tokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new TokenException("Invalid token."));

        if (token.getTokenType() != expectedType) {
            throw new TokenException("Invalid token type.");
        }

        if (token.getConfirmedAt() != null) {
            throw new TokenException("Token has already been used.");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenException("Token has expired.");
        }

        return token;
    }
    
    private String buildEmail(String title, String message, String link, String buttonText) {
        return "<div style='font-family: Arial, sans-serif; text-align: center; color: #333;'>"
                + "<h2>" + title + "</h2>"
                + "<p>" + message + "</p>"
                + "<a href='" + link + "' style='background-color: #007BFF; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 20px;'>"
                + buttonText
                + "</a>"
                + "<p style='margin-top: 30px; font-size: 0.8em;'>If you did not request this, please ignore this email.</p>"
                + "</div>";
    }

    public boolean verifyCode(String secret, int code) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.authorize(secret, code);
    }

}
