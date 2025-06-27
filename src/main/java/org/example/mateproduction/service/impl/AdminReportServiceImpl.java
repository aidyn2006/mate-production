package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mateproduction.dto.request.UpdateReportStatusRequest;
import org.example.mateproduction.dto.response.*;
import org.example.mateproduction.entity.*;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ResourceNotFoundException;
import org.example.mateproduction.repository.*;
import org.example.mateproduction.service.AdminReportService;
import org.example.mateproduction.service.UserService;
import org.example.mateproduction.util.ReportStatus;
import org.example.mateproduction.util.ReportableType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportServiceImpl implements AdminReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final AdHouseRepository adHouseRepository;
    private final AdSeekerRepository adSeekerRepository;
    private final UserService userService;

    @Override
    public Page<ReportResponse> getAllReports(ReportStatus status, Pageable pageable) {
        Page<Report> reports = (status != null)
                ? reportRepository.findByStatus(status, pageable)
                : reportRepository.findAll(pageable);
        return reports.map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDetailResponse getReportById(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
        return toDetailDto(report);
    }

    @Override
    @Transactional
    public ReportResponse resolveReport(UUID reportId, UpdateReportStatusRequest request) {
        // 1. Get the DTO of the current user to find their ID
        UserResponse currentUserResponse = userService.getCurrentUser();

        // 2. Fetch the full User ENTITY from the database using the ID
        User currentUserEntity = userRepository.findById(currentUserResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found in database with id: " + currentUserResponse.getId()));

        // 3. Find the report to be updated
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

        // 4. Update the report with the fetched ENTITY
        report.setStatus(request.getStatus());
        report.setResolutionNotes(request.getResolutionNotes());
        report.setResolvedBy(currentUserEntity); // Now this works correctly

        Report updatedReport = reportRepository.save(report);
        return toDto(updatedReport);
    }



    private ReportDetailResponse toDetailDto(Report report) {
        Object reportedContent = getReportedContent(report.getReportedEntityType(), report.getReportedEntityId());

        return ReportDetailResponse.builder()
                .id(report.getId())
                .reporter(mapToUserResponse(report.getReporter()))
                .reportedEntityId(report.getReportedEntityId())
                .reportedEntityType(report.getReportedEntityType())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .resolvedByAdminId(report.getResolvedBy() != null ? report.getResolvedBy().getId() : null)
                .resolutionNotes(report.getResolutionNotes())
                .createdAt(report.getCreatedAt() != null ? report.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null)
                .reportedContent(reportedContent)
                .build();
    }
    
    private Object getReportedContent(ReportableType type, UUID entityId) {
        switch (type) {
            case AD_HOUSE:
                return adHouseRepository.findById(entityId).map(this::mapAdHouseToResponse).orElse(null);
            case AD_SEEKER:
                return adSeekerRepository.findById(entityId).map(this::mapAdSeekerToResponse).orElse(null);
            case USER:
                return userRepository.findById(entityId).map(this::mapToUserResponse).orElse(null);
            default:
                return null;
        }
    }
    private ReportResponse toDto(Report report) {
        if (report == null) {
            return null;
        }
        return ReportResponse.builder()
                .id(report.getId())
                .reporterId(report.getReporter() != null ? report.getReporter().getId() : null)
                .reportedEntityId(report.getReportedEntityId())
                .reportedEntityType(report.getReportedEntityType())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .resolvedByAdminId(report.getResolvedBy() != null ? report.getResolvedBy().getId() : null)
                .resolutionNotes(report.getResolutionNotes())
                .createdAt(report.getCreatedAt() != null ? report.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null)
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isVerified(user.getIsVerified())
                .avatarUrl(user.getAvatarUrl())
                .isDeleted(user.getIsDeleted())
                .createdAt(user.getCreatedAt())
                .status(user.getStatus())
                .banReason(user.getBanReason())
                .build();
    }

    private AdHouseResponse mapAdHouseToResponse(AdHouse adHouse) {
        if (adHouse == null) {
            return null;
        }
        return AdHouseResponse.builder()
                .id(adHouse.getId())
                .title(adHouse.getTitle())
                .description(adHouse.getDescription())
                .price(adHouse.getPrice())
                .address(adHouse.getAddress())
                .city(adHouse.getCity())
                .user(mapToUserResponse(adHouse.getUser())) // nested mapping
                .type(adHouse.getType())
                .status(adHouse.getStatus())
                .images(adHouse.getImages())
                .numberOfRooms(adHouse.getNumberOfRooms())
                .area(adHouse.getArea())
                .floor(adHouse.getFloor())
                .furnished(adHouse.getFurnished())
                .contactPhoneNumber(adHouse.getContactPhoneNumber())
                .views(adHouse.getViews())
                .typeOfAd(adHouse.getTypeOfAd())
                .createdAt(adHouse.getCreatedAt())
                .updatedAt(adHouse.getUpdatedAt())
                .mainImageUrl(adHouse.getMainImageUrl())
                .moderationComment(adHouse.getModerationComment())
                .build();
    }

    private AdSeekerResponse mapAdSeekerToResponse(AdSeeker adSeeker) {
        if (adSeeker == null) {
            return null;
        }
        return AdSeekerResponse.builder()
                .id(adSeeker.getId())
                .age(adSeeker.getAge())
                .gender(adSeeker.getGender())
                .user(mapToUserResponse(adSeeker.getUser())) // nested mapping
                .seekerDescription(adSeeker.getSeekerDescription())
                .city(adSeeker.getCity())
                .desiredLocation(adSeeker.getDesiredLocation())
                .maxBudget(adSeeker.getMaxBudget())
                .moveInDate(adSeeker.getMoveInDate())
                .hasFurnishedPreference(adSeeker.getHasFurnishedPreference())
                .roommatePreferences(adSeeker.getRoommatePreferences())
                .preferredRoommateGender(adSeeker.getPreferredRoommateGender())
                .status(adSeeker.getStatus())
                .views(adSeeker.getViews())
                .typeOfAd(adSeeker.getTypeOfAd())
                .contactPhoneNumber(adSeeker.getContactPhoneNumber())
                .createdAt(adSeeker.getCreatedAt())
                .updatedAt(adSeeker.getUpdatedAt())
                .moderationComment(adSeeker.getModerationComment())
                .build();
    }


    // You should move your mappers to a dedicated mapper class, but for simplicity, I'll keep them here.
    // ... include mapToUserResponse, mapAdHouseToResponse, mapAdSeekerToResponse, and toDto (for ReportResponse) mappers here ...
}