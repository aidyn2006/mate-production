package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.AdminUserUpdateRequest;
import org.example.mateproduction.dto.request.BanRequest;
import org.example.mateproduction.dto.request.UpdateReportStatusRequest;
import org.example.mateproduction.dto.request.UserRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.ReportResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AdminUserService {
    Page<UserResponse> getAllUsers(String email, String status, Pageable pageable);

    UserResponse getUserById(UUID userId) throws NotFoundException;

    UserResponse updateUser(UUID userId, UserRequest request) throws NotFoundException;

    UserResponse updateUser(UUID userId, AdminUserUpdateRequest request) throws NotFoundException;

    void deleteUserSoft(UUID userId) throws NotFoundException;

    void deleteUserHard(UUID userId) throws NotFoundException;

    void banUser(UUID userId, BanRequest banRequest) throws NotFoundException;

    void unbanUser(UUID userId) throws NotFoundException;

    void verifyUser(UUID userId) throws NotFoundException;

    List<AdHouseResponse> getUserHouseAds(UUID userId);

    List<AdSeekerResponse> getUserSeekerAds(UUID userId);

    Page<ReportResponse> getAllReports(Pageable pageable);

    ReportResponse getReportById(UUID reportId);

    ReportResponse updateReportStatus(UUID reportId, UpdateReportStatusRequest request);

    List<ReportResponse> getUserReports(UUID userId);
}
