package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import org.example.mateproduction.config.Jwt.JwtService;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.dto.request.LoginRequest;
import org.example.mateproduction.dto.request.RegisterRequest;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.AlreadyExistException;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.AuthService;
import org.example.mateproduction.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service

public class AuthServiceImpl implements AuthService {
    private final UserRepository ur;
    private final PasswordEncoder pe;
    private final JwtService js;
    private final AuthenticationManager am;

    @Autowired
    public AuthServiceImpl(UserRepository ur, PasswordEncoder pe, JwtService js, AuthenticationManager am) {
        this.ur = ur;
        this.pe = pe;
        this.js = js;
        this.am = am;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest r) throws AlreadyExistException {
        if (ur.findByEmail(r.getEmail()).isPresent())
            throw new AlreadyExistException("User already exists");
        var u = User.builder()
                .email(r.getEmail())
                .password(pe.encode(r.getPassword()))
                .role(Role.valueOf(r.getRole()))
                .name(r.getName())
                .surname(r.getSurname())
                .phone(r.getPhone())
                .build();
        ur.save(u);
        var token = js.generateToken(new JwtUserDetails(u));
        return UserResponse.builder()
                .id(u.getId()).fullName(u.getName()+" "+u.getSurname())
                .email(u.getEmail()).phone(u.getPhone())
                .username(u.getName()).role(u.getRole().name())
                .token(token).build();
    }

    @Override
    public UserResponse login(LoginRequest req) {
        am.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        var u = ur.findByEmail(req.getEmail()).orElseThrow();
        var token = js.generateToken(new JwtUserDetails(u));
        return UserResponse.builder()
                .id(u.getId()).fullName(u.getName()+" "+u.getSurname())
                .email(u.getEmail()).phone(u.getPhone())
                .username(u.getName()).role(u.getRole().name())
                .token(token).build();
    }
}
