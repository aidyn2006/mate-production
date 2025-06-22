package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.dto.request.AdHouseFilter;
import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.repository.AdHouseRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.AdHouseService;
import org.example.mateproduction.specification.AdHouseSpecification;
import org.example.mateproduction.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequiredArgsConstructor
public class AdHouseServiceImpl implements AdHouseService {

    private final AdHouseRepository adHouseRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public Page<AdHouseResponse> getAllAds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdHouse> adsPage = adHouseRepository.findAllByStatus(Status.ACTIVE, pageable);
        return adsPage.map(AdHouseServiceImpl::mapToResponseDto);
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

        return ads.map(AdHouseServiceImpl::mapToResponseDto);
    }

    @Override
    public Page<AdHouseResponse> searchAds(AdHouseFilter filter, Pageable pageable) {
        // Create the dynamic specification based on the filter DTO
        Specification<AdHouse> spec = AdHouseSpecification.findByCriteria(filter);

        // The repository's findAll method now does everything: filtering, pagination, and sorting!
        Page<AdHouse> adHousePage = adHouseRepository.findAll(spec, pageable);

        // Map the result page to our response DTO
        return adHousePage.map(AdHouseServiceImpl::mapToResponseDto); // Assuming you have a static mapper
    }


    @Override
    @Transactional
    public AdHouseResponse getAdById(UUID adId) throws NotFoundException {
        AdHouse ad = adHouseRepository.findByIdAndStatus(adId, Status.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Ad is not available"));
        return mapToResponseDto(ad);
    }

    @Override
    @Transactional
    public AdHouseResponse createAd(AdHouseRequest dto) throws ValidationException, NotFoundException {
        UUID currentUserId = getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        validateAdRequest(dto);

        int activeAdsCount = adHouseRepository.countByUserAndStatus(user, Status.ACTIVE);
        if (activeAdsCount >= 10) {
            throw new ValidationException("User reached active ads limit");
        }

        AdHouse ad = AdHouse.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .address(dto.getAddress())
                .city(dto.getCity())
                .user(user)
                .type(dto.getType())
                .status(Status.ACTIVE)
                .numberOfRooms(dto.getNumberOfRooms())
                .area(dto.getArea())
                .floor(dto.getFloor())
                .furnished(dto.getFurnished())
                .contactPhoneNumber(dto.getContactPhoneNumber())
                .views(0)
                .build();

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            ad.setImages(uploadImages(dto.getImages()));
        }

        ad = adHouseRepository.save(ad);
        return mapToResponseDto(ad);
    }

    @Override
    @Transactional
    public AdHouseResponse updateAd(UUID adId, AdHouseRequest dto) throws NotFoundException, AccessDeniedException, ValidationException {
        UUID currentUserId = getCurrentUserId();

        AdHouse ad = adHouseRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        if (!ad.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User not authorized to update this ad");
        }

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

        // This logic is now safe because uploadImages returns a mutable list
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            // First, you might want to delete old images from Cloudinary if they are being replaced
            // (This is an enhancement, not part of the bug fix)
            // cloudinaryService.deleteImages(ad.getImages());

            ad.setImages(uploadImages(dto.getImages()));
        }

        ad = adHouseRepository.save(ad); // This line will now succeed
        return mapToResponseDto(ad);
    }

    @Override
    @Transactional
    public void deleteAd(UUID adId) throws AccessDeniedException, NotFoundException {
        UUID currentUserId = getCurrentUserId();

        AdHouse ad = adHouseRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        if (!ad.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User not authorized to delete this ad");
        }

        ad.setStatus(Status.DELETED);
        adHouseRepository.save(ad);
    }

    // --------------------- ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ -----------------------

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
        // CORRECTED: Collect to a new ArrayList, which is mutable
        return images.stream()
                .map(cloudinaryService::upload)
                .collect(Collectors.toCollection(ArrayList::new));
    }



    private static AdHouseResponse mapToResponseDto(AdHouse ad) {
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
                .status(ad.getStatus())
                .images(ad.getImages() != null ? ad.getImages() : Collections.emptyList())
                .numberOfRooms(ad.getNumberOfRooms())
                .area(ad.getArea())
                .floor(ad.getFloor())
                .furnished(ad.getFurnished())
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
