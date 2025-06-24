package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.mateproduction.dto.request.CreateReportRequest;
import org.example.mateproduction.dto.request.UpdateReportStatusRequest;
import org.example.mateproduction.dto.response.ReportResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.Report;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ReportAlreadyExistsException;
import org.example.mateproduction.exception.ResourceNotFoundException;
import org.example.mateproduction.exception.UnauthorizedException;
import org.example.mateproduction.repository.AdHouseRepository;
import org.example.mateproduction.repository.AdSeekerRepository;
import org.example.mateproduction.repository.ReportRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.ReportService;
import org.example.mateproduction.service.UserService;
import org.example.mateproduction.util.ReportableType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final AdHouseRepository adHouseRepository; // Assuming you have this
    private final AdSeekerRepository adSeekerRepository; // Assuming you have this
    private final UserService userService;


    @Override
    @Transactional
    public ReportResponse createReport(CreateReportRequest request, UserDetails userDetails) throws UnauthorizedException {
        if (userDetails == null) {
            log.warn("Attempt to create a report without authentication.");
            throw new UnauthorizedException("User is not authenticated");
        }
        UUID reporterId = userService.getCurrentUser().getId();
        if (reporterId == null) {
            log.warn("Could not find current user from security context.");
            throw new UnauthorizedException("User is not authenticated");
        }

        log.info("Attempting to create report by user {} for entity {} of type {}",
                reporterId, request.getReportedEntityId(), request.getReportedEntityType());
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", reporterId);
                    return new ResourceNotFoundException("Reporter with ID " + reporterId + " not found.");
                });

        validateReportableEntityExists(request.getReportedEntityId(), request.getReportedEntityType());

        // 3. Prevent duplicate reports
        reportRepository.findByReporterIdAndReportedEntityIdAndReportedEntityType(
            reporterId, request.getReportedEntityId(), request.getReportedEntityType()
        ).ifPresent(existingReport -> {
            log.warn("User {} already reported entity {} of type {}. Report ID: {}",
                    reporterId, request.getReportedEntityId(), request.getReportedEntityType(), existingReport.getId());
            throw new ReportAlreadyExistsException("You have already reported this content.");
        });

        // 4. Build and save the report
        Report report = Report.builder()
                .reporter(reporter)
                .reportedEntityId(request.getReportedEntityId())
                .reportedEntityType(request.getReportedEntityType())
                .reason(request.getReason())
                .description(request.getDescription())
                // Status defaults to PENDING via @Builder.Default in the entity
                .build();

        Report savedReport = reportRepository.save(report);
        log.info("Successfully created report with ID: {}", savedReport.getId());

        return toDto(savedReport);
    }



    private void validateReportableEntityExists(UUID entityId, ReportableType type) {
        boolean exists;
        switch (type) {
            case AD_HOUSE:
                exists = adHouseRepository.existsById(entityId);
                break;
            case AD_SEEKER:
                exists = adSeekerRepository.existsById(entityId);
                break;
            // Add cases for future reportable types here
            default:
                log.error("Validation failed for unknown reportable type: {}", type);
                throw new IllegalArgumentException("Unsupported reportable entity type: " + type);
        }
        if (!exists) {
            log.error("Reportable entity of type {} with ID {} not found.", type, entityId);
            throw new ResourceNotFoundException("The content you are trying to report does not exist.");
        }
    }

    private ReportResponse toDto(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .reporterId(report.getReporter().getId())
                .reportedEntityId(report.getReportedEntityId())
                .reportedEntityType(report.getReportedEntityType())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .resolvedByAdminId(report.getResolvedBy() != null ? report.getResolvedBy().getId() : null)
                .resolutionNotes(report.getResolutionNotes())
                .createdAt(report.getCreatedAt() != null ?
                        report.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null)
                .build();
    }
}