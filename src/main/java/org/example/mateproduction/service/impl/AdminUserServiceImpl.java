package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mateproduction.dto.request.UpdateReportStatusRequest;
import org.example.mateproduction.dto.request.UserRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.ReportResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.AdSeeker;
import org.example.mateproduction.entity.Report;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ResourceNotFoundException;
import org.example.mateproduction.repository.AdHouseRepository;
import org.example.mateproduction.repository.AdSeekerRepository;
import org.example.mateproduction.repository.ReportRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.AdminUserService;
import org.example.mateproduction.service.UserService;
import org.example.mateproduction.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final AdHouseRepository adHouseRepository;
    private final AdSeekerRepository adSeekerRepository;
    private final ReportRepository reportRepository;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;


    @Override
    public List<UserResponse> getAllUsers(boolean includeDeleted) {
        List<User> users = includeDeleted
                ? userRepository.findAll()
                : userRepository.findAll().stream()
                .filter(user -> !Boolean.TRUE.equals(user.getIsDeleted()))
                .toList();

        return users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(UUID userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(UUID userId, UserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            user.setAvatarUrl(cloudinaryService.upload(request.getAvatar()));
        }

        userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    public void deleteUserSoft(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void deleteUserHard(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public List<AdHouseResponse> getUserHouseAds(UUID userId) {
        List<AdHouse> ads = adHouseRepository.findAllByUserId(userId);
        return ads.stream()
                .filter(ad -> ad.getStatus() != Status.DELETED)
                .map(this::mapAdHouseToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdSeekerResponse> getUserSeekerAds(UUID userId) {
        List<AdSeeker> ads = adSeekerRepository.findAllByUserId(userId);
        return ads.stream()
                .filter(ad -> ad.getStatus() != Status.DELETED)
                .map(this::mapAdSeekerToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public Page<ReportResponse> getAllReports(Pageable pageable) {
        log.info("Fetching all reports for page request: {}", pageable);
        // Use the toDto mapper for each element in the page
        return reportRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public ReportResponse getReportById(UUID reportId) {
        log.info("Fetching report with ID: {}", reportId);
        return reportRepository.findById(reportId)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Report with ID " + reportId + " not found."));
    }

    @Override
    @Transactional
    public ReportResponse updateReportStatus(UUID reportId, UpdateReportStatusRequest request) {
        UUID adminId = userService.getCurrentUser().getId();

        log.info("Admin {} updating status of report {} to {}", adminId, reportId, request.getStatus());

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report with ID " + reportId + " not found."));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin with ID " + adminId + " not found."));

        report.setStatus(request.getStatus());
        report.setResolutionNotes(request.getResolutionNotes());
        report.setResolvedBy(admin);

        Report updatedReport = reportRepository.save(report);
        log.info("Successfully updated status for report {}", updatedReport.getId());
        return toDto(updatedReport);
    }

    // ------------------ Мапперы ----------------------
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

    private UserResponse mapToResponse(User user) {
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
                .build();
    }

    private AdHouseResponse mapAdHouseToResponse(AdHouse ad) {
        return AdHouseResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .price(ad.getPrice())
                .address(ad.getAddress())
                .city(ad.getCity())
                .user(mapToResponse(ad.getUser()))
                .type(ad.getType())
                .mainImageUrl(ad.getMainImageUrl())
                .status(ad.getStatus())
                .images(ad.getImages())
                .numberOfRooms(ad.getNumberOfRooms())
                .area(ad.getArea())
                .floor(ad.getFloor())
                .furnished(ad.getFurnished())
                .contactPhoneNumber(ad.getContactPhoneNumber())
                .views(ad.getViews())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .moderationComment(ad.getModerationComment())
                .build();
    }

    private AdSeekerResponse mapAdSeekerToResponse(AdSeeker ad) {
        return AdSeekerResponse.builder()
                .id(ad.getId())
                .age(ad.getAge())
                .gender(ad.getGender())
                .user(mapToResponse(ad.getUser()))
                .seekerDescription(ad.getSeekerDescription())
                .city(ad.getCity())
                .desiredLocation(ad.getDesiredLocation())
                .maxBudget(ad.getMaxBudget())
                .moveInDate(ad.getMoveInDate())
                .hasFurnishedPreference(ad.getHasFurnishedPreference())
                .roommatePreferences(ad.getRoommatePreferences())
                .preferredRoommateGender(ad.getPreferredRoommateGender())
                .status(ad.getStatus())
                .views(ad.getViews())
                .contactPhoneNumber(ad.getContactPhoneNumber())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .build();
    }
}
