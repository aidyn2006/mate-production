package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdminListingDetailResponse;
import org.example.mateproduction.dto.response.AdminReasonResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.repository.AdHouseRepository;
import org.example.mateproduction.service.AdminListingService;
import org.example.mateproduction.util.Status;
import org.example.mateproduction.util.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("adminHouseService")
@RequiredArgsConstructor
@Transactional
public class AdminHouseAdServiceImpl implements AdminListingService<AdHouse> {

    private final AdHouseRepository adHouseRepository;
    private CloudinaryService cloudinaryService;

    @Override
    public JpaRepository<AdHouse, UUID> getRepository() {
        return adHouseRepository;
    }

    @Override
    public Page<AdHouseResponse> findAll(UUID userId, Status status, Pageable pageable) {
        Specification<AdHouse> spec = Specification.where(null);
        if (userId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        return adHouseRepository.findAll(spec, pageable).map(this::mapToResponseDto);
    }

    @Override
    public AdminListingDetailResponse findById(UUID adId) {
        AdHouse ad = adHouseRepository.findById(adId)
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
        adHouseRepository.deleteById(adId);
    }

    @Override
    public void featureAd(UUID adId) {
        AdHouse ad = adHouseRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad id: " + adId));
        ad.setFeatured(true);
        adHouseRepository.save(ad);
    }

    @Override
    public void unfeatureAd(UUID adId) {
        AdHouse ad = adHouseRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad id: " + adId));
        ad.setFeatured(false);
        adHouseRepository.save(ad);
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
            ArrayList<String> strings = new ArrayList<>();
            return strings; // Return an empty mutable list
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
                .typeOfAd(ad.getTypeOfAd())

                .build();
    }

    private AdminListingDetailResponse mapToDetailResponseDto(AdHouse ad) {
        return AdminListingDetailResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .price(ad.getPrice())
                .address(ad.getAddress())
                .city(ad.getCity())
                .user(buildUserResponse(ad.getUser())) // Use a helper for UserResponse
                .type(ad.getType())
                .mainImageUrl(ad.getMainImageUrl())
                .status(ad.getStatus())
                .images(ad.getImages())
                .numberOfRooms(ad.getNumberOfRooms())
                .area(ad.getArea())
                .floor(ad.getFloor())
                .furnished(ad.isFeatured())
                .contactPhoneNumber(ad.getContactPhoneNumber())
                .views(ad.getViews())
                .typeOfAd(Type.HOUSE) // Make sure to set this
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .moderationComment(ad.getModerationComment())
                .featured(ad.isFeatured())
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