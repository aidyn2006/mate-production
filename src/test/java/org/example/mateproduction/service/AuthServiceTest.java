package org.example.mateproduction.service;

import org.example.mateproduction.config.Jwt.JwtService;
import org.example.mateproduction.dto.request.LoginRequest;
import org.example.mateproduction.dto.request.RegisterRequest;
import org.example.mateproduction.dto.request.Verify2FARequest;
import org.example.mateproduction.dto.response.LoginResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.Token;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.TokenRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.impl.AuthServiceImpl;
import org.example.mateproduction.service.impl.CloudinaryService;
import org.example.mateproduction.util.TokenType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.example.mateproduction.exception.AlreadyExistException;



@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private EmailService emailService;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;


    @InjectMocks
    @Spy
    private AuthServiceImpl authService;


    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("123456")
                .name("Test")
                .surname("User")
                .username("testuser")
                .phone("1234567890")
                .avatar(null)
                .build();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = authService.register(request);

        // then
        assertNotNull(response);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendEmail(eq("test@example.com"), anyString(), contains("Verify"));
    }

    @Test
    void shouldThrowAlreadyExistExceptionWhenEmailExists() {
        // given
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .build();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendEmail(any(), any(), any());
    }


    @Test
    void shouldLoginSuccessfullyWithout2FA() throws NotFoundException {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("123456");

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setIsTwoFaEnabled(false);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));


        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertFalse(response.isTwoFactorEnabled());
        assertNotNull(response.getUser());

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@gmail.com");
    }


    @Test
    void shouldVerifyAccountSuccessfully() {
        // given
        String tokenStr = "abc123";

        User user = new User();
        user.setIsVerified(false);

        Token token = new Token();
        token.setToken(tokenStr);
        token.setTokenType(TokenType.EMAIL_VERIFICATION);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setUser(user);

        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        authService.verifyAccount(tokenStr);

        assertTrue(user.getIsVerified());
        assertNotNull(token.getConfirmedAt());

        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
    }


    @Test
    void shouldSuccessfullyVerify2FA() throws NotFoundException {
        // given
        Verify2FARequest request = new Verify2FARequest();
        request.setEmail("test@gmail.com");
        request.setCode(123456);

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setTwoFaSecret("SECRET123");

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        doReturn(true).when(authService).verifyCode("SECRET123", 123456);


        UserResponse actualResponse = authService.verify2FA(request);

        assertNotNull(actualResponse);
        assertEquals("test@gmail.com", actualResponse.getEmail());

        verify(userRepository).findByEmail("test@gmail.com");
        verify(authService).verifyCode("SECRET123", 123456);
    }


}




