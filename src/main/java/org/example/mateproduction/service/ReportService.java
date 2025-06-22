package org.example.mateproduction.service;


import org.example.mateproduction.dto.request.CreateReportRequest;
import org.example.mateproduction.dto.request.UpdateReportStatusRequest;
import org.example.mateproduction.dto.response.ReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReportService {
    // For Users
    /**
     * Creates a new report for a given entity.
     *
     * @param request The DTO containing report details.
     * @param reporterId The ID of the user submitting the report.
     * @return A DTO representing the newly created report.
     */
    ReportResponse createReport(CreateReportRequest request, UUID reporterId);

    // For Admins
    Page<ReportResponse> getAllReports(Pageable pageable);

    ReportResponse getReportById(UUID reportId);

    ReportResponse updateReportStatus(UUID reportId, UpdateReportStatusRequest request, UUID adminId);
}