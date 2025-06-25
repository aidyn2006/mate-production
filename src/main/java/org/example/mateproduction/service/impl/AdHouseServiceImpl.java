package org.example.mateproduction.service.impl;

import jakarta.servlet.http.HttpServletRequest;
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
import org.example.mateproduction.util.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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



    @Transactional
    public Page<AdHouseResponse> getAllAds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdHouse> adsPage = adHouseRepository.findAllByStatus(Status.ACTIVE, pageable);
        return adsPage.map(this::toResponseDto); // Use method reference
    }

    /**
     * @deprecated This method uses a fixed number of parameters for filtering.
     * The searchAds method using Specifications is more flexible and preferred.
     */
    @Deprecated
    public Page<AdHouseResponse> findByFilter(AdHouseFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdHouse> ads = adHouseRepository.findByFilter(filter.getMinPrice(), filter.getMaxPrice(), filter.getMinRooms(), filter.getMaxRooms(), filter.getMinArea(), filter.getMaxArea(), filter.getCity(), filter.getType(), filter.getFurnished(), filter.getStatus(), pageable);
        return ads.map(this::toResponseDto);
    }

    @Override
    @Transactional
    public void updateMainImage(UUID adId, String mainImageUrl) throws NotFoundException, AccessDeniedException, ValidationException {
        UUID currentUserId = getCurrentUserId();
        AdHouse ad = adHouseRepository.findById(adId).orElseThrow(() -> new NotFoundException("Ad not found"));

        if (!ad.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User not authorized to update this ad");
        }

        if (ad.getImages() == null || !ad.getImages().contains(mainImageUrl)) {
            throw new ValidationException("Main image must be one of the ad images");
        }

        ad.setMainImageUrl(mainImageUrl);
        adHouseRepository.save(ad);
    }

    @Override
    public Page<AdHouseResponse> searchAds(AdHouseFilter filter, Pageable pageable) {
        Specification<AdHouse> spec = AdHouseSpecification.findByCriteria(filter);
        Page<AdHouse> adHousePage = adHouseRepository.findAll(spec, pageable);
        return adHousePage.map(this::toResponseDto); // Use method reference
    }

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

        return toResponseDto(ad);
    }


    @Override
    @Transactional
    public AdHouseResponse createAd(AdHouseRequest dto) throws ValidationException, NotFoundException {
        UUID currentUserId = getCurrentUserId();
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new NotFoundException("User not found"));

        validateAdRequest(dto);

        if (adHouseRepository.countByUserAndStatus(user, Status.ACTIVE) >= 10) {
            throw new ValidationException("User reached active ads limit");
        }

        // Use the private helper method to create the entity
        AdHouse ad = toEntity(dto);
        ad.setUser(user);
        ad.setStatus(Status.MODERATION);
        ad.setTypeOfAd(Type.HOUSE);
        ad.setViews(0);

        List<String> uploadedUrls = new ArrayList<>();
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            uploadedUrls.addAll(uploadImages(dto.getImages()));
        }
        ad.setImages(uploadedUrls);

        if (StringUtils.hasText(dto.getMainImageUrl()) && !uploadedUrls.isEmpty()) {
            final String mainImageName = dto.getMainImageUrl();
            final String publicId = mainImageName.contains(".") ? mainImageName.substring(0, mainImageName.lastIndexOf('.')) : mainImageName;

            Optional<String> mainUrl = uploadedUrls.stream()
                    .filter(url -> url.contains(publicId))
                    .findFirst();

            ad.setMainImageUrl(mainUrl.orElse(uploadedUrls.get(0)));
        } else if (!uploadedUrls.isEmpty()) {
            ad.setMainImageUrl(uploadedUrls.get(0));
        }

        ad = adHouseRepository.save(ad);
        // Use the private helper method to create the response
        return toResponseDto(ad);
    }


    @Override
    @Transactional
    public AdHouseResponse updateAd(UUID adId, AdHouseRequest dto) throws NotFoundException, AccessDeniedException, ValidationException {
        UUID currentUserId = getCurrentUserId();
        AdHouse ad = adHouseRepository.findById(adId).orElseThrow(() -> new NotFoundException("Ad not found"));

        if (!ad.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User not authorized to update this ad");
        }

        validateAdRequest(dto);

        // Use the private helper method to update the entity
        updateEntityFromDto(dto, ad);
        ad.setStatus(Status.MODERATION);

        List<String> existingImageUrls = new ArrayList<>(ad.getImages());
        List<String> newImageUrls = new ArrayList<>();

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            newImageUrls.addAll(uploadImages(dto.getImages()));
        }

        List<String> allImageUrls = new ArrayList<>();
        allImageUrls.addAll(existingImageUrls);
        allImageUrls.addAll(newImageUrls);
        ad.setImages(allImageUrls);

        if (StringUtils.hasText(dto.getMainImageUrl())) {
            final String mainImageIdentifier = dto.getMainImageUrl();

            if (allImageUrls.contains(mainImageIdentifier)) {
                ad.setMainImageUrl(mainImageIdentifier);
            } else {
                final String publicId = mainImageIdentifier.contains(".") ? mainImageIdentifier.substring(0, mainImageIdentifier.lastIndexOf('.')) : mainImageIdentifier;
                Optional<String> mainUrl = newImageUrls.stream()
                        .filter(url -> url.contains(publicId))
                        .findFirst();
                mainUrl.ifPresent(ad::setMainImageUrl);
            }
        } else if (!allImageUrls.isEmpty() && !StringUtils.hasText(ad.getMainImageUrl())) {
            ad.setMainImageUrl(allImageUrls.get(0));
        }

        ad = adHouseRepository.save(ad);
        return toResponseDto(ad);
    }

    @Override
    @Transactional
    public void deleteAd(UUID adId) throws AccessDeniedException, NotFoundException {
        UUID currentUserId = getCurrentUserId();
        AdHouse ad = adHouseRepository.findById(adId).orElseThrow(() -> new NotFoundException("Ad not found"));

        if (!ad.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User not authorized to delete this ad");
        }

        ad.setStatus(Status.DELETED);
        adHouseRepository.save(ad);
    }


    // --------------------- MAPPING HELPER METHODS -----------------------

    /**
     * Converts an AdHouse entity to an AdHouseResponse DTO.
     */
    private AdHouseResponse toResponseDto(AdHouse ad) {
        if (ad == null) {
            return null;
        }

        // Create the nested UserResponse object
        UserResponse userResponse = UserResponse.builder()
                .id(ad.getUser().getId())
                .name(ad.getUser().getName())
                .surname(ad.getUser().getSurname())
                .username(ad.getUser().getUsername())
                .email(ad.getUser().getEmail())
                .phone(ad.getUser().getPhone())
                .role(ad.getUser().getRole())
                .isVerified(ad.getUser().getIsVerified())
                .avatarUrl(ad.getUser().getAvatarUrl())
                .build();

        return AdHouseResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .price(ad.getPrice())
                .address(ad.getAddress())
                .city(ad.getCity())
                .user(userResponse) // Set the nested user DTO
                .type(ad.getType())
                .status(ad.getStatus())
                .images(ad.getImages() != null ? ad.getImages() : Collections.emptyList())
                .numberOfRooms(ad.getNumberOfRooms())
                .area(ad.getArea())
                .floor(ad.getFloor())
                .furnished(ad.getFurnished())
                .contactPhoneNumber(ad.getContactPhoneNumber())
                .views(ad.getViews())
                .typeOfAd(ad.getTypeOfAd())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .mainImageUrl(ad.getMainImageUrl())
                .moderationComment(ad.getModerationComment())
                .build();
    }

    /**
     * Creates a new AdHouse entity from an AdHouseRequest DTO.
     * Business-specific fields like user, status, etc., are set in the service method.
     */
    private AdHouse toEntity(AdHouseRequest dto) {
        if (dto == null) {
            return null;
        }
        return AdHouse.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .address(dto.getAddress())
                .city(dto.getCity())
                .type(dto.getType())
                .contactPhoneNumber(dto.getContactPhoneNumber())
                .numberOfRooms(dto.getNumberOfRooms())
                .area(dto.getArea())
                .floor(dto.getFloor())
                .furnished(dto.getFurnished())
                .build();
    }

    /**
     * Updates an existing AdHouse entity with fields from an AdHouseRequest DTO.
     */
    private void updateEntityFromDto(AdHouseRequest dto, AdHouse ad) {
        if (dto == null || ad == null) {
            return;
        }
        ad.setTitle(dto.getTitle());
        ad.setDescription(dto.getDescription());
        ad.setPrice(dto.getPrice());
        ad.setAddress(dto.getAddress());
        ad.setCity(dto.getCity());
        ad.setType(dto.getType());
        ad.setContactPhoneNumber(dto.getContactPhoneNumber());
        ad.setNumberOfRooms(dto.getNumberOfRooms());
        ad.setArea(dto.getArea());
        ad.setFloor(dto.getFloor());
        ad.setFurnished(dto.getFurnished());
    }

    // --------------------- OTHER HELPER METHODS -----------------------

    private void validateAdRequest(AdHouseRequest dto) throws ValidationException {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) throw new ValidationException("Title is required");
        if (dto.getDescription() == null || dto.getDescription().isBlank()) throw new ValidationException("Description is required");
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) throw new ValidationException("Price must be greater than zero");
        if (dto.getCity() == null) throw new ValidationException("City is required");
        if (dto.getType() == null) throw new ValidationException("Ad type is required");
        if (dto.getContactPhoneNumber() == null || dto.getContactPhoneNumber().isBlank()) throw new ValidationException("Contact phone number is required");
    }

    private List<String> uploadImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }
        return images.stream()
                .map(cloudinaryService::upload)
                .collect(Collectors.toList());
    }

    private UUID getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("User is not authenticated");
        }

        var principal = authentication.getPrincipal();
        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new SecurityException("Invalid user principal type");
    }
    }