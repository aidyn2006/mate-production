package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.dto.request.AdSeekerFilter;
import org.example.mateproduction.dto.request.AdSeekerRequest;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.AdSeeker;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.repository.AdSeekerRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.AdSeekerService;
import org.example.mateproduction.specification.AdSeekerSpecification;
import org.example.mateproduction.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdSeekerServiceImpl implements AdSeekerService {

    private final AdSeekerRepository adSeekerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Page<AdSeekerResponse> getAllAds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return adSeekerRepository.findAllByStatus(Status.ACTIVE, pageable)
                .map(this::mapToResponseDto);
    }



    @Override
    @Transactional
    public AdSeekerResponse getAdById(UUID adId) throws NotFoundException {
        AdSeeker ad = adSeekerRepository.findByIdAndStatus(adId, Status.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Ad not found"));
        return mapToResponseDto(ad);
    }

    @Override
    @Transactional
    public AdSeekerResponse createAd(AdSeekerRequest dto) throws ValidationException, NotFoundException {
        UUID currentUserId = getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        validateAdSeekerRequest(dto);

        int activeAds = adSeekerRepository.countByUserAndStatus(user, Status.ACTIVE);
        if (activeAds >= 10) {
            throw new ValidationException("You have reached the maximum number of active ads");
        }

        AdSeeker ad = AdSeeker.builder()
                .age(dto.getAge())
                .gender(dto.getGender())
                .seekerDescription(dto.getSeekerDescription())
                .user(user)
                .city(dto.getCity())
                .desiredLocation(dto.getDesiredLocation())
                .maxBudget(dto.getMaxBudget())
                .moveInDate(dto.getMoveInDate())
                .hasFurnishedPreference(dto.getHasFurnishedPreference())
                .roommatePreferences(dto.getRoommatePreferences())
                .preferredRoommateGender(dto.getPreferredRoommateGender())
                .contactPhoneNumber(dto.getContactPhoneNumber())
                .status(Status.ACTIVE)
                .views(0)
                .build();

        ad = adSeekerRepository.save(ad);
        return mapToResponseDto(ad);
    }

    @Override
    @Transactional
    public AdSeekerResponse updateAd(UUID adId, AdSeekerRequest dto) throws NotFoundException, AccessDeniedException, ValidationException {
        UUID currentUserId = getCurrentUserId();

        AdSeeker ad = adSeekerRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        if (!ad.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not authorized to update this ad");
        }

        validateAdSeekerRequest(dto);

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

        ad = adSeekerRepository.save(ad);
        return mapToResponseDto(ad);
    }

    @Override
    @Transactional
    public void deleteAd(UUID adId) throws NotFoundException, AccessDeniedException {
        UUID currentUserId = getCurrentUserId();

        AdSeeker ad = adSeekerRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        if (!ad.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not authorized to delete this ad");
        }

        ad.setStatus(Status.DELETED);
        adSeekerRepository.save(ad);
    }

    @Override
    public Page<AdSeekerResponse> findByFilter(AdSeekerFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Specification<AdSeeker> spec = AdSeekerSpecification.build(filter);

        Page<AdSeeker> seekers = adSeekerRepository.findAll(spec, pageable);
        return seekers.map(this::mapToResponseDto);
    }




    // ------------------- ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ----------------------

    private void validateAdSeekerRequest(AdSeekerRequest dto) throws ValidationException {
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
            throw new ValidationException("Phone number must be valid Kazakhstan format like +77071234567");
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
                .roommatePreferences(ad.getRoommatePreferences() != null ? ad.getRoommatePreferences() : Collections.emptyList())
                .preferredRoommateGender(ad.getPreferredRoommateGender())
                .status(ad.getStatus())
                .views(ad.getViews())
                .contactPhoneNumber(ad.getContactPhoneNumber())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .build();
    }

    private UUID getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new SecurityException("User is not authenticated");
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }

        throw new SecurityException("Invalid user principal");
    }
}
