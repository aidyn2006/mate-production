package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.AdSeekerRequest;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.AdminListingDetailResponse;
import org.example.mateproduction.dto.response.AdminReasonResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.AdSeeker;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.repository.AdSeekerRepository;
import org.example.mateproduction.service.AdminListingService;
import org.example.mateproduction.util.Status;
import org.example.mateproduction.util.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service("adminSeekerService")
@RequiredArgsConstructor
@Transactional
public class AdminSeekerAdServiceImpl implements AdminListingService<AdSeeker> {

    private final AdSeekerRepository adSeekerRepository;

    @Override
    public JpaRepository<AdSeeker, UUID> getRepository() {
        return adSeekerRepository;
    }

    @Override
    public Page<AdSeekerResponse> findAll(UUID userId, Status status, Pageable pageable) {
        Specification<AdSeeker> spec = Specification.where(null);
        if (userId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        return adSeekerRepository.findAll(spec, pageable).map(this::mapToResponseDto);
    }

    @Override
    public AdminListingDetailResponse findById(UUID adId) {
        AdSeeker ad = adSeekerRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad id: " + adId));
        return mapToDetailResponseDto(ad);
    }

    @Override
    public void approveAd(UUID adId) {
        moderate(adId, Status.ACTIVE, null);
    }

    @Override
    public AdminReasonResponse rejectAd(UUID adId, String reason) {
        moderate(adId, Status.REJECTED, reason);
        return new AdminReasonResponse(adId, reason);
    }

    @Override
    public void deleteAd(UUID adId) {
        adSeekerRepository.deleteById(adId);
    }

    @Override
    public void featureAd(UUID adId) {
        AdSeeker ad = adSeekerRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad id: " + adId));
        ad.setFeatured(true);
        adSeekerRepository.save(ad);
    }

    @Override
    public void unfeatureAd(UUID adId) {
        AdSeeker ad = adSeekerRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad id: " + adId));
        ad.setFeatured(false);
        adSeekerRepository.save(ad);
    }



    @Transactional
    public AdSeekerResponse updateAd(UUID adId, AdSeekerRequest dto) throws NotFoundException, AccessDeniedException, ValidationException {
        AdSeeker ad = adSeekerRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        validateAdRequest(dto);

        ad.setAge(dto.getAge());
        ad.setGender(dto.getGender());
        ad.setSeekerDescription(dto.getSeekerDescription());
        ad.setCity(dto.getCity());
        ad.setDesiredLocation(dto.getDesiredLocation());
        ad.setMaxBudget(dto.getMaxBudget());
        ad.setMoveInDate(dto.getMoveInDate());
        ad.setHasFurnishedPreference(dto.getHasFurnishedPreference());
        ad.setRoommatePreferences(dto.getRoommatePreferences());
        ad.setPreferredRoommateGender(dto.getPreferredRoommateGender());
        ad.setContactPhoneNumber(dto.getContactPhoneNumber());
        ad.setStatus(Status.MODERATION);

        ad = adSeekerRepository.save(ad);
        return mapToResponseDto(ad);
    }

    private void validateAdRequest(AdSeekerRequest dto) throws ValidationException {
        if (dto.getAge() == null || dto.getAge() < 16 || dto.getAge() > 100)
            throw new ValidationException("Age must be between 16 and 100");

        if (dto.getGender() == null)
            throw new ValidationException("Gender is required");

        if (dto.getSeekerDescription() == null || dto.getSeekerDescription().isBlank())
            throw new ValidationException("Description is required");

        if (dto.getCity() == null)
            throw new ValidationException("City is required");

        if (dto.getMaxBudget() == null || dto.getMaxBudget().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Max budget must be greater than 0");

        if (dto.getMoveInDate() == null)
            throw new ValidationException("Move-in date is required");

        if (dto.getContactPhoneNumber() == null || dto.getContactPhoneNumber().isBlank())
            throw new ValidationException("Phone number is required");

        if (!dto.getContactPhoneNumber().matches("^\\+?77\\d{9}$"))
            throw new ValidationException("Phone number must be in valid Kazakhstan format like +77071234567");
    }

    private AdSeekerResponse mapToResponseDto(AdSeeker ad) {
        return AdSeekerResponse.builder()
                .id(ad.getId())
                .age(ad.getAge())
                .gender(ad.getGender())
                .user(UserResponse.builder()
                        .id(ad.getUser().getId())
                        .name(ad.getUser().getName())
                        .surname(ad.getUser().getSurname())
                        .username(ad.getUser().getUsername())
                        .email(ad.getUser().getEmail())
                        .phone(ad.getUser().getPhone())
                        .role(ad.getUser().getRole())
                        .isVerified(ad.getUser().getIsVerified())
                        .avatarUrl(ad.getUser().getAvatarUrl())
                        .build())
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
                .typeOfAd(ad.getTypeOfAd())
                .moderationComment(ad.getModerationComment())
                .build();
    }

    private AdminListingDetailResponse mapToDetailResponseDto(AdSeeker ad) {
        return AdminListingDetailResponse.builder()
                .user(buildUserResponse(ad.getUser())) // Use a helper for UserResponse
                .status(ad.getStatus())
                .moderationComment(ad.getModerationComment())
                .featured(ad.isFeatured())
                .views(ad.getViews())
                .contactPhoneNumber(ad.getContactPhoneNumber())
                .typeOfAd(Type.SEEKER)
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .gender(ad.getGender())
                .seekerDescription(ad.getSeekerDescription())
                .desiredLocation(ad.getDesiredLocation())
                .maxBudget(ad.getMaxBudget())
                .moveInDate(ad.getMoveInDate())
                .hasFurnishedPreference(ad.getHasFurnishedPreference())
                .roommatePreferences(ad.getRoommatePreferences())
                .preferredRoommateGender(ad.getPreferredRoommateGender())
                .age(ad.getAge())
                .build();

    }


    private UserResponse buildUserResponse(User user) {
        // This helper can be moved to a separate mapper class
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
}