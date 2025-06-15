package org.example.mateproduction.controller;


import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.LoginRequest;
import org.example.mateproduction.dto.request.RegisterRequest;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.exception.AlreadyExistException;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@ModelAttribute RegisterRequest request) throws AlreadyExistException {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request) throws NotFoundException {
        return ResponseEntity.ok(authService.login(request));
    }
}
