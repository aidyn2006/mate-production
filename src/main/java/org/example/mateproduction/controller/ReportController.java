package org.example.mateproduction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mateproduction.dto.request.CreateReportRequest;
import org.example.mateproduction.dto.response.ReportResponse;
import org.example.mateproduction.exception.UnauthorizedException;
import org.example.mateproduction.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@Valid @RequestBody CreateReportRequest createReportRequest, @AuthenticationPrincipal UserDetails userDetails) throws UnauthorizedException {
        ReportResponse createdReport = reportService.createReport(createReportRequest, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
    }

}