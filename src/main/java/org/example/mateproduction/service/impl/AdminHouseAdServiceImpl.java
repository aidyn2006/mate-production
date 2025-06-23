package org.example.mateproduction.service.impl;

import org.example.mateproduction.dto.request.AdHouseFilter;
import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdminReasonResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.repository.AdHouseRepository;
import org.example.mateproduction.service.AdminHouseAdService;
import org.example.mateproduction.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class AdminHouseAdServiceImpl implements AdminHouseAdService {

    private final AdHouseRepository adHouseRepository;
    private final CloudinaryService cloudinaryService;



    @Autowired
    public AdminHouseAdServiceImpl(AdHouseRepository adHouseRepository, CloudinaryService cloudinaryService) {
        this.adHouseRepository = adHouseRepository;
        this.cloudinaryService = cloudinaryService;
    }


    @Transactional
    public Page<AdHouseResponse> getAllModerateAds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdHouse> adsPage = adHouseRepository.findAllByStatus(Status.MODERATION, pageable);
        return adsPage.map(this::mapToResponseDto);
    }

    public Page<AdHouseResponse> findByFilter(AdHouseFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<AdHouse> ads = adHouseRepository.findByFilter(
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getMinRooms(),
                filter.getMaxRooms(),
                filter.getMinArea(),
                filter.getMaxArea(),
                filter.getCity(),
                filter.getType(),
                filter.getFurnished(),
                filter.getStatus(),
                pageable
        );

        return ads.map(this::mapToResponseDto);
    }


    @Transactional
    public void approveAd(UUID adId) {
        AdHouse adHouse = adHouseRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad id: " + adId));

        if (!adHouse.getStatus().equals(Status.MODERATION)) {
            throw new IllegalStateException("Ad is not under moderation");
        }

        adHouse.setStatus(Status.ACTIVE);
        adHouseRepository.save(adHouse);
    }

    @Transactional
    public AdminReasonResponse rejectAd(UUID adId, String reason) {
        AdHouse adHouse=adHouseRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad id: " + adId));

        if (!adHouse.getStatus().equals(Status.MODERATION)) {
            throw new IllegalStateException("Ad is not under moderation");
        }

        adHouse.setStatus(Status.REJECTED);
        adHouse.setModerationComment(reason);
        adHouseRepository.save(adHouse);
        return AdminReasonResponse.builder()
                .id(adHouse.getId())
                .reason(reason)
                .build();
    }

    @Transactional
    public AdHouseResponse updateAd(UUID adId, AdHouseRequest dto) throws NotFoundException, AccessDeniedException, ValidationException {
        AdHouse ad = adHouseRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        validateAdRequest(dto);

        ad.setTitle(dto.getTitle());
        ad.setDescription(dto.getDescription());
        ad.setPrice(dto.getPrice());
        ad.setAddress(dto.getAddress());
        ad.setCity(dto.getCity());
        ad.setType(dto.getType());
        ad.setNumberOfRooms(dto.getNumberOfRooms());
        ad.setArea(dto.getArea());
        ad.setFloor(dto.getFloor());
        ad.setFurnished(dto.getFurnished());
        ad.setContactPhoneNumber(dto.getContactPhoneNumber());
        ad.setStatus(Status.MODERATION);

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<String> uploadedImages = uploadImages(dto.getImages());
            ad.setImages(uploadedImages);
            ad.setMainImageUrl(uploadedImages.get(0));
        }

        ad = adHouseRepository.save(ad);
        return mapToResponseDto(ad);
    }
    @Transactional
    public void changeAdStatus(UUID adId, Status newStatus, String reason) {
        AdHouse adHouse = adHouseRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad id: " + adId));

        // если статус не меняется — можно не сохранять
        if (adHouse.getStatus() == newStatus) {
            return;
        }

        adHouse.setStatus(newStatus);

        // если статус REJECTED или MODERATION — сохраняем комментарий
        if (newStatus == Status.REJECTED || newStatus == Status.MODERATION) {
            adHouse.setModerationComment(reason);
        } else {
            // сбрасываем комментарий если другой статус
            adHouse.setModerationComment(null);
        }

        adHouseRepository.save(adHouse);
    }



    private void validateAdRequest(AdHouseRequest dto) throws ValidationException {
        if (dto.getTitle() == null || dto.getTitle().isBlank())
            throw new ValidationException("Title is required");
        if (dto.getDescription() == null || dto.getDescription().isBlank())
            throw new ValidationException("Description is required");
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Price must be greater than zero");
        if (dto.getCity() == null)
            throw new ValidationException("City is required");
        if (dto.getType() == null)
            throw new ValidationException("Ad type is required");
        if (dto.getContactPhoneNumber() == null || dto.getContactPhoneNumber().isBlank())
            throw new ValidationException("Contact phone number is required");
    }

    private List<String> uploadImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>(); // Return an empty mutable list
        }
        return images.stream()
                .map(cloudinaryService::upload)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private AdHouseResponse mapToResponseDto(AdHouse ad) {
        return AdHouseResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .price(ad.getPrice())
                .address(ad.getAddress())
                .city(ad.getCity())
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
                .type(ad.getType())
                .mainImageUrl(ad.getMainImageUrl())
                .status(ad.getStatus())
                .images(ad.getImages() != null ? ad.getImages() : Collections.emptyList())
                .numberOfRooms(ad.getNumberOfRooms())
                .area(ad.getArea())
                .floor(ad.getFloor())
                .furnished(ad.getFurnished())
                .contactPhoneNumber(ad.getContactPhoneNumber())
                .createdAt(ad.getCreatedAt())
                .moderationComment(ad.getModerationComment())
                .updatedAt(ad.getUpdatedAt())
                .build();
    }
}
