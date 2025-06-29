package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.ChangePasswordRequest;
import org.example.mateproduction.dto.request.UserRequest;
import org.example.mateproduction.dto.response.*;
import org.example.mateproduction.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID userId);
    void deleteHardById(UUID userId);
    void deleteById(UUID userId);
    List<UserResponse> getAllUsers();
    UserResponse getCurrentUser();
    List<AdHouseResponse> getAllAdHouses();
    List<AdSeekerResponse> getAllAdSeekers();

    UUID getCurrentUserId();
    UserResponse updateUser(UUID userId, UserRequest request);
    void changePassword(UUID userId, ChangePasswordRequest request);
    PublicUserResponse getPublicById(UUID userId) throws NotFoundException;

    DashboardSummaryResponse getDashboardSummary();
}
