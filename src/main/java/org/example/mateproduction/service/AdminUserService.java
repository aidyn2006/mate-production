package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.UserRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface AdminUserService {
    List<UserResponse> getAllUsers(boolean includeDeleted);

    UserResponse getUserById(UUID userId) throws NotFoundException;

    UserResponse updateUser(UUID userId, UserRequest request);

    void deleteUserSoft(UUID userId);

    void deleteUserHard(UUID userId);

    List<AdHouseResponse> getUserHouseAds(UUID userId);

    List<AdSeekerResponse> getUserSeekerAds(UUID userId);
}
