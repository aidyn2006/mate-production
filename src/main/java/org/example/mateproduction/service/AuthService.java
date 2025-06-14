package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.LoginRequest;
import org.example.mateproduction.dto.request.RegisterRequest;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.exception.AlreadyExistException;

public interface AuthService {

    UserResponse register(RegisterRequest request) throws AlreadyExistException;
    UserResponse login(LoginRequest request);
}
