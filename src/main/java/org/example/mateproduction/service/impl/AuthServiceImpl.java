package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtService;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.dto.request.LoginRequest;
import org.example.mateproduction.dto.request.RegisterRequest;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.AlreadyExistException;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.AuthService;
import org.example.mateproduction.util.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
                .build();

        userRepository.save(user);

        return buildUserResponse(user);
    }

    @Override
    public UserResponse login(LoginRequest request) throws NotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with this email"));

        return buildUserResponse(user);
    }

    private UserResponse buildUserResponse(User user) {
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
                .build();
    }
}
