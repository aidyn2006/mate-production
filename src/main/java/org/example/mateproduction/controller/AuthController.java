package org.example.mateproduction.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.ForgotPasswordRequest;
import org.example.mateproduction.dto.request.LoginRequest;
import org.example.mateproduction.dto.request.RegisterRequest;
import org.example.mateproduction.dto.request.ResetPasswordRequest;
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

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        authService.verifyAccount(token);
        return ResponseEntity.ok("Account verified successfully. You can now log in.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) throws NotFoundException {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("A password reset link has been sent to your email address.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password has been reset successfully.");
    }
}
