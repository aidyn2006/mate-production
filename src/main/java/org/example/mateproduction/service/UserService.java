package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.ChangePasswordRequest;
import org.example.mateproduction.dto.request.UserRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID userId);
    void deleteById(UUID userId);
    List<UserResponse> getAllUsers();
    UserResponse getCurrentUser();
    List<AdHouseResponse> getAllAdHouses();
    List<AdSeekerResponse> getAllAdSeekers();

    UserResponse updateUser(UUID userId, UserRequest request);
    void changePassword(ChangePasswordRequest request);
    void verifyUser(String token);
    void banUser(UUID userId);
}
