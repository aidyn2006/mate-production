package org.example.mateproduction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.AdminUserUpdateRequest;
import org.example.mateproduction.dto.request.BanRequest;
import org.example.mateproduction.dto.request.UpdateReportStatusRequest;
import org.example.mateproduction.dto.request.UserRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.ReportResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(adminUserService.getAllUsers(email, status, pageable));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) throws NotFoundException {
        return ResponseEntity.ok(adminUserService.getUserById(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @ModelAttribute UserRequest request
    ) throws NotFoundException {
        return ResponseEntity.ok(adminUserService.updateUser(userId, request));
    }

    // In your AdminController.java
    @PutMapping("/users/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @RequestBody AdminUserUpdateRequest request // Use @RequestBody and the new DTO
    ) throws NotFoundException {
        return ResponseEntity.ok(adminUserService.updateUser(userId, request));
    }

    // Мягкое удаление пользователя
    @DeleteMapping("/{userId}/soft")
    public ResponseEntity<Void> softDeleteUser(@PathVariable UUID userId) throws NotFoundException {
        adminUserService.deleteUserSoft(userId);
        return ResponseEntity.noContent().build();
    }

    // Жёсткое удаление пользователя
    @DeleteMapping("/{userId}/hard")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable UUID userId) throws NotFoundException {
        adminUserService.deleteUserHard(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable UUID userId, @RequestBody(required = false) BanRequest banRequest) throws NotFoundException {
        adminUserService.banUser(userId, banRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable UUID userId) throws NotFoundException {
        adminUserService.unbanUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/verify")
    public ResponseEntity<Void> verifyUser(@PathVariable UUID userId) throws NotFoundException {
        adminUserService.verifyUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/house-ads")
    public ResponseEntity<List<AdHouseResponse>> getUserHouseAds(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminUserService.getUserHouseAds(userId));
    }

    @GetMapping("/{userId}/seeker-ads")
    public ResponseEntity<List<AdSeekerResponse>> getUserSeekerAds(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminUserService.getUserSeekerAds(userId));
    }

    @GetMapping("/reports")
    public ResponseEntity<Page<ReportResponse>> getAllReports(Pageable pageable) {
        Page<ReportResponse> reports = adminUserService.getAllReports(pageable);
        return ResponseEntity.ok(reports);
    }

//    @GetMapping("/{reportId}")
//    public ResponseEntity<ReportResponse> getReportById(@PathVariable UUID reportId) {
//        ReportResponse report = adminUserService.getReportById(reportId);
//        return ResponseEntity.ok(report);
//    }

    @PatchMapping("/{reportId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable UUID reportId,
            @Valid @RequestBody UpdateReportStatusRequest request) {
        ReportResponse updatedReport = adminUserService.updateReportStatus(reportId, request);
        return ResponseEntity.ok(updatedReport);
    }

    @GetMapping("/{userId}/reports")
    public ResponseEntity<List<ReportResponse>> getUserReports(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminUserService.getUserReports(userId));
    }
}
