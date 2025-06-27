package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.UpdateReportStatusRequest;
import org.example.mateproduction.dto.response.ReportDetailResponse;
import org.example.mateproduction.dto.response.ReportResponse;
import org.example.mateproduction.util.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminReportService {
    Page<ReportResponse> getAllReports(ReportStatus status, Pageable pageable);
    ReportDetailResponse getReportById(UUID reportId);
    ReportResponse resolveReport(UUID reportId, UpdateReportStatusRequest request);
}