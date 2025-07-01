package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.LoginRequest;
import org.example.mateproduction.dto.request.RegisterRequest;
import org.example.mateproduction.dto.request.ResetPasswordRequest;
import org.example.mateproduction.dto.request.Verify2FARequest;
import org.example.mateproduction.dto.response.LoginResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.exception.AlreadyExistException;
import org.example.mateproduction.exception.NotFoundException;

public interface AuthService {

    UserResponse register(RegisterRequest request) throws AlreadyExistException;
    LoginResponse login(LoginRequest request) throws NotFoundException;
    void verifyAccount(String token);
    void forgotPassword(String email) throws NotFoundException;
    void resetPassword(ResetPasswordRequest request);
    UserResponse verify2FA(Verify2FARequest request) throws NotFoundException;
}
