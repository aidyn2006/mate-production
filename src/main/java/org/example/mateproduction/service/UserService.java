package org.example.mateproduction.service;

import org.example.mateproduction.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID userId);
    void deleteById(UUID userId);
    List<UserResponse> getAllUsers();
    UserResponse getCurrentUser();
}
