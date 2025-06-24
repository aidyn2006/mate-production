package org.example.mateproduction.service;


import org.example.mateproduction.dto.request.CreateReportRequest;
import org.example.mateproduction.dto.request.UpdateReportStatusRequest;
import org.example.mateproduction.dto.response.ReportResponse;
import org.example.mateproduction.exception.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface ReportService {

    ReportResponse createReport(CreateReportRequest request,UserDetails userDetails) throws UnauthorizedException;

}