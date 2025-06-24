package org.example.mateproduction.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mateproduction.dto.request.UpdateReportStatusRequest;
import org.example.mateproduction.dto.request.BanRequest;
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
import org.example.mateproduction.util.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
    // Assuming you have a CloudinaryService for image uploads.
    // private final CloudinaryService cloudinaryService;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;


    @Override
    public Page<UserResponse> getAllUsers(String email, String status, Pageable pageable) {
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (StringUtils.hasText(email)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(status)) {
                try {
                    UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), userStatus));
                } catch (IllegalArgumentException e) {
                    // Optionally log a warning that the status value is invalid
                    System.err.println("Invalid status value received: " + status);
                }
            }
            return predicate;
        };
        Page<User> users = userRepository.findAll(spec, pageable);
        return users.map(this::mapToResponse);
    }

    @Override
    public UserResponse getUserById(UUID userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(UUID userId, UserRequest request) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());

        // Assuming you have a CloudinaryService for uploads
        // if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
        //     user.setAvatarUrl(cloudinaryService.upload(request.getAvatar()));
        // }

        userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    public void deleteUserSoft(UUID userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        user.setIsDeleted(true);
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    @Override
    public void deleteUserHard(UUID userId) throws NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public void banUser(UUID userId, BanRequest banRequest) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        user.setStatus(UserStatus.BANNED);
        user.setBanReason(banRequest != null ? banRequest.getReason() : "No reason provided.");
        userRepository.save(user);
    }

    @Override
    public void unbanUser(UUID userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Revert to VERIFIED if they were, otherwise ACTIVE.
        if (Boolean.TRUE.equals(user.getIsVerified())) {
            user.setStatus(UserStatus.VERIFIED);
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }
        user.setBanReason(null); // Clear the ban reason
        userRepository.save(user);
    }

    @Override
    public void verifyUser(UUID userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        user.setIsVerified(true);
        // Only update status to VERIFIED if they are currently ACTIVE.
        // This prevents unbanning a user just by verifying them.
        if (user.getStatus() == UserStatus.ACTIVE) {
            user.setStatus(UserStatus.VERIFIED);
        }
        userRepository.save(user);
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

    @Override
    public List<ReportResponse> getUserReports(UUID userId) {
        return reportRepository.findAllByReporterId(userId).stream()
                .map(this::reportToResponse)
                .collect(Collectors.toList());
    }


    // ------------------ Mappers ----------------------
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
                .isDeleted(user.getIsDeleted())
                .createdAt(user.getCreatedAt())
                .status(user.getStatus()) // Mapped new field
                .banReason(user.getBanReason()) // Mapped new field
                .status(user.getStatus())
                .banReason(user.getBanReason())
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

    private ReportResponse reportToResponse(Report report) {
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
