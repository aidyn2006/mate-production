package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.base.Ad;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.repository.AdHouseRepository;
import org.example.mateproduction.service.impl.AdHouseServiceImpl;
import org.example.mateproduction.util.AdType;
import org.example.mateproduction.util.CityNames;
import org.example.mateproduction.util.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.example.mateproduction.exception.AlreadyExistException;


@ExtendWith(MockitoExtension.class)
public class AdHouseServiceTest {



    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdHouseRepository adHouseRepository;


    @InjectMocks
    @Spy
    private AdHouseServiceImpl adHouseService;


    @Test
    void shouldCreateSuccesfullAdHouseAd() throws ValidationException, NotFoundException {
        AdHouseRequest adHouseRequest = new AdHouseRequest();
        adHouseRequest.setTitle("Test title");
        adHouseRequest.setDescription("Test description");
        adHouseRequest.setPrice(new BigDecimal("100000"));
        adHouseRequest.setCity(CityNames.ASTANA);
        adHouseRequest.setType(AdType.MONTH);
        adHouseRequest.setContactPhoneNumber("87001112233");

        UUID fakeId = UUID.randomUUID();
        User user = new User();
        user.setIsVerified(true);
        user.setId(fakeId);

        when(userService.getCurrentUserId()).thenReturn(fakeId);
        when(userRepository.findById(fakeId)).thenReturn(Optional.of(user));
        when(adHouseRepository.countByUserAndStatus(user, Status.ACTIVE)).thenReturn(5); // < 10
        when(adHouseRepository.save(any(AdHouse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdHouseResponse response = adHouseService.createAd(adHouseRequest);

        assertNotNull(response);
        verify(userService).getCurrentUserId();
        verify(adHouseRepository).save(any(AdHouse.class));
    }




}
