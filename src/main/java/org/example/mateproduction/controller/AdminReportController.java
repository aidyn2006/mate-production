package org.example.mateproduction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.UpdateReportStatusRequest;
import org.example.mateproduction.dto.response.ReportDetailResponse;
import org.example.mateproduction.dto.response.ReportResponse;
import org.example.mateproduction.service.AdminReportService;
import org.example.mateproduction.util.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping
    public ResponseEntity<Page<ReportResponse>> getAllReports(
            @RequestParam(name = "status", required = false) ReportStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(adminReportService.getAllReports(status, pageable));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDetailResponse> getReportById(@PathVariable UUID reportId) {
        return ResponseEntity.ok(adminReportService.getReportById(reportId));
    }

    @PostMapping("/{reportId}/resolve")
    public ResponseEntity<ReportResponse> resolveReport(
            @PathVariable UUID reportId,
            @Valid @RequestBody UpdateReportStatusRequest request) {
        return ResponseEntity.ok(adminReportService.resolveReport(reportId, request));
    }
}