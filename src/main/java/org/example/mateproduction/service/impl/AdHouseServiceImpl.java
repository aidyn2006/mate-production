package org.example.mateproduction.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.AdHouseFilter;
import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.helpers.Auditable;
import org.example.mateproduction.repository.AdHouseRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.AdHouseService;
import org.example.mateproduction.service.UserService;
import org.example.mateproduction.specification.AdHouseSpecification;
import org.example.mateproduction.util.Status;
import org.example.mateproduction.util.Type;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdHouseServiceImpl implements AdHouseService {

    private final AdHouseRepository adHouseRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final RedisService redisService;
    private final UserService userService;

    @Override
    @Transactional
    public Page<AdHouseResponse> getAllAds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return adHouseRepository.findAllByStatus(Status.ACTIVE, pageable)
                .map(AdHouseServiceImpl::mapToResponseDto);
    }

    @Override
    public Page<AdHouseResponse> findByFilter(AdHouseFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return adHouseRepository.findByFilter(
                        filter.getMinPrice(), filter.getMaxPrice(),
                        filter.getMinRooms(), filter.getMaxRooms(),
                        filter.getMinArea(), filter.getMaxArea(),
                        filter.getCity(), filter.getType(),
                        filter.getFurnished(), filter.getStatus(), pageable)
                .map(AdHouseServiceImpl::mapToResponseDto);
    }

    @Override
    public Page<AdHouseResponse> searchAds(AdHouseFilter filter, Pageable pageable) {
        Specification<AdHouse> spec = AdHouseSpecification.findByCriteria(filter);
        return adHouseRepository.findAll(spec, pageable)
                .map(AdHouseServiceImpl::mapToResponseDto);
    }

    @Override
    @Transactional
    public AdHouseResponse getAdById(UUID adId, HttpServletRequest request) throws NotFoundException {
        AdHouse ad = adHouseRepository.findByIdAndStatus(adId, Status.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Ad is not available"));

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        if (!redisService.isViewCounted(adId, ip, userAgent)) {
            redisService.incrementViews(adId);
            ad.setViews(ad.getViews() + 1);
            adHouseRepository.save(ad);
        }

        return mapToResponseDto(ad);
    }

    @Override
    @Transactional
    @Auditable(action = "CREATE_HOUSE_AD")
    public AdHouseResponse createAd(AdHouseRequest dto) throws ValidationException, NotFoundException {
        UUID currentUserId = userService.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        validateAdRequest(dto);

        if (adHouseRepository.countByUserAndStatus(user, Status.ACTIVE) >= 10) {
            throw new ValidationException("User reached active ads limit");
        }

        if (!user.getIsVerified()) {
            throw new ValidationException("User is not verified");
        }

        AdHouse ad = AdHouse.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .address(dto.getAddress())
                .city(dto.getCity())
                .user(user)
                .type(dto.getType())
                .status(Status.MODERATION)
                .numberOfRooms(dto.getNumberOfRooms())
                .area(dto.getArea())
                .floor(dto.getFloor())
                .typeOfAd(Type.HOUSE)
                .furnished(dto.getFurnished())
                .contactPhoneNumber(dto.getContactPhoneNumber())
                .views(0)
                .build();

        List<String> uploadedImages = uploadImages(dto.getImages());
        if (!uploadedImages.isEmpty()) {
            ad.setImages(uploadedImages);
            ad.setMainImageUrl(uploadedImages.get(0));
        }

        return mapToResponseDto(adHouseRepository.save(ad));
    }

    @Override
    @Transactional
    public AdHouseResponse updateAd(UUID adId, AdHouseRequest dto)
            throws NotFoundException, AccessDeniedException, ValidationException {
        UUID currentUserId = userService.getCurrentUserId();

        AdHouse ad = adHouseRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        if (ad.getStatus() == Status.DELETED) {
            throw new ValidationException("Cannot update deleted ad");
        }

        checkUserOwnership(currentUserId, ad);
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

        List<String> uploaded = uploadImages(dto.getImages());
        if (!uploaded.isEmpty()) {
            ad.setImages(uploaded);
            ad.setMainImageUrl(uploaded.get(0));
        }

        return mapToResponseDto(adHouseRepository.save(ad));
    }

    @Override
    @Transactional
    public void deleteAd(UUID adId) throws AccessDeniedException, NotFoundException {
        UUID currentUserId = userService.getCurrentUserId();

        AdHouse ad = adHouseRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        checkUserOwnership(currentUserId, ad);

        ad.setStatus(Status.DELETED);
        adHouseRepository.save(ad);
    }

    @Override
    @Transactional
    public void updateMainImage(UUID adId, String mainImageUrl)
            throws NotFoundException, AccessDeniedException, ValidationException {
        UUID currentUserId = userService.getCurrentUserId();

        AdHouse ad = adHouseRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        checkUserOwnership(currentUserId, ad);

        if (ad.getImages() == null || ad.getImages().isEmpty()) {
            throw new ValidationException("No images available for this ad");
        }

        if (!ad.getImages().contains(mainImageUrl)) {
            throw new ValidationException("Main image must be one of the ad images");
        }

        ad.setMainImageUrl(mainImageUrl);
        adHouseRepository.save(ad);
    }

    // -------------------- ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ --------------------

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

    private void checkUserOwnership(UUID currentUserId, AdHouse ad) throws AccessDeniedException {
        if (!ad.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User not authorized to modify this ad");
        }
    }

    private List<String> uploadImages(List<MultipartFile> images) {
        return images == null || images.isEmpty()
                ? new ArrayList<>()
                : images.stream().map(cloudinaryService::upload).collect(Collectors.toList());
    }

    private static AdHouseResponse mapToResponseDto(AdHouse ad) {
        User user = ad.getUser();
        return AdHouseResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .price(ad.getPrice())
                .address(ad.getAddress())
                .city(ad.getCity())
                .user(UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .surname(user.getSurname())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .role(user.getRole())
                        .isVerified(user.getIsVerified())
                        .avatarUrl(user.getAvatarUrl())
                        .build())
                .type(ad.getType())
                .status(ad.getStatus())
                .mainImageUrl(ad.getMainImageUrl())
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
}
