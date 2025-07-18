package org.example.mateproduction.service;


import org.example.mateproduction.dto.request.CreateReportRequest;
import org.example.mateproduction.dto.response.ReportResponse;
import org.example.mateproduction.exception.UnauthorizedException;
import org.springframework.security.core.userdetails.UserDetails;

public interface ReportService {

    ReportResponse createReport(CreateReportRequest request, UserDetails userDetails) throws UnauthorizedException;

}