package org.example.mateproduction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mateproduction.dto.request.CreateReportRequest;
import org.example.mateproduction.dto.request.UpdateReportStatusRequest;
import org.example.mateproduction.dto.response.ReportResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.service.ReportService;
import org.example.mateproduction.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;
    private final UserService userService; // You would need a service to get your User entity

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @Valid @RequestBody CreateReportRequest createReportRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            // This case should ideally be handled by Spring Security config
            log.warn("Attempt to create a report without authentication.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Assuming your UserDetails implementation can give you the custom User entity or its ID.
        UserResponse currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            // This case might occur if token is invalid but somehow passes security filter
            log.warn("Could not find current user from security context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Received report request from user: {}", currentUser.getUsername());

        ReportResponse createdReport = reportService.createReport(createReportRequest, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
    }

    // GET all reports (paginated) - for ADMIN
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReportResponse>> getAllReports(Pageable pageable) {
        Page<ReportResponse> reports = reportService.getAllReports(pageable);
        return ResponseEntity.ok(reports);
    }

    // GET a single report by ID - for ADMIN
    @GetMapping("/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable UUID reportId) {
        ReportResponse report = reportService.getReportById(reportId);
        return ResponseEntity.ok(report);
    }

    // PATCH to update a report's status - for ADMIN
    @PatchMapping("/{reportId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable UUID reportId,
            @Valid @RequestBody UpdateReportStatusRequest request) {

        UserResponse adminUser = userService.getCurrentUser();
        ReportResponse updatedReport = reportService.updateReportStatus(reportId, request, adminUser.getId());
        return ResponseEntity.ok(updatedReport);
    }

}