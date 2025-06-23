package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.AdSeekerFilter;
import org.example.mateproduction.dto.request.AdSeekerRequest;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.AdminReasonResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.AdSeeker;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.repository.AdSeekerRepository;
import org.example.mateproduction.service.AdminSeekerAdService;
import org.example.mateproduction.util.Status;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSeekerAdServiceImpl implements AdminSeekerAdService {

    private final AdSeekerRepository adSeekerRepository;

    @Override
    @Transactional
    public Page<AdSeekerResponse> getAllModerateAds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return adSeekerRepository.findAllByStatus(Status.MODERATION, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public void approveAd(UUID adId) {
        AdSeeker ad = adSeekerRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad id: " + adId));

        if (!ad.getStatus().equals(Status.MODERATION)) {
            throw new IllegalStateException("Ad is not under moderation");
        }

        ad.setStatus(Status.ACTIVE);
        adSeekerRepository.save(ad);
    }

    @Override
    @Transactional
    public AdminReasonResponse rejectAd(UUID adId, String reason) {
        AdSeeker ad = adSeekerRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad id: " + adId));

        if (!ad.getStatus().equals(Status.MODERATION)) {
            throw new IllegalStateException("Ad is not under moderation");
        }

        ad.setStatus(Status.REJECTED);
        ad.setModerationComment(reason);
        adSeekerRepository.save(ad);

        return AdminReasonResponse.builder()
                .id(ad.getId())
                .reason(reason)
                .build();
    }

    @Override
    @Transactional
    public void changeAdStatus(UUID adId, Status newStatus, String reason) {
        AdSeeker ad = adSeekerRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad id: " + adId));

        if (ad.getStatus() == newStatus) {
            return;
        }

        ad.setStatus(newStatus);

        if (newStatus == Status.REJECTED || newStatus == Status.MODERATION) {
            ad.setModerationComment(reason);
        } else {
            ad.setModerationComment(null);
        }

        adSeekerRepository.save(ad);
    }

    @Override
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
                .build();
    }
}
