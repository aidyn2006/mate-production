package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.AdRequest;
import org.example.mateproduction.dto.response.AdImageResponse;
import org.example.mateproduction.dto.response.AdResponse;
import org.example.mateproduction.dto.response.CityResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.Ad;
import org.example.mateproduction.entity.AdImage;
import org.example.mateproduction.entity.City;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.repository.AdImageRepository;
import org.example.mateproduction.repository.AdRepository;
import org.example.mateproduction.repository.CityRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.AdService;
import org.example.mateproduction.util.AdType;
import org.example.mateproduction.util.Status;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final AdImageRepository adImageRepository;


    public List<AdResponse> getAllAds() {
        List<Ad> ads=adRepository.findAll();
        return ads.stream()
                .map(AdServiceImpl::mapToResponseDto)
                .collect(Collectors.toList());    }


    @Transactional
    public AdResponse createAd(AdRequest dto, UUID userId) throws ValidationException, NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        validateAdRequest(dto);

        City city = cityRepository.findById(dto.getCity())
                .orElseThrow(() -> new NotFoundException("City not found"));

        int activeAdsCount = adRepository.countByUserAndStatus(user, Status.ACTIVE);
        if (activeAdsCount >= 10) {
            throw new ValidationException("User reached active ads limit");
        }

        Ad ad = Ad.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .address(dto.getAddress())
                .city(city)
                .user(user)
                .type(AdType.valueOf(String.valueOf(dto.getType())))
                .status(Status.ACTIVE)
                .numberOfRooms(dto.getNumberOfRooms())
                .area(dto.getArea())
                .floor(dto.getFloor())
                .furnished(dto.getFurnished())
                .roommatePreferences(dto.getRoommatePreferences())
                .isSharedRoom(dto.getIsSharedRoom())
                .seekerDescription(dto.getSeekerDescription())
                .desiredLocation(dto.getDesiredLocation())
                .desiredRoommatePreferences(dto.getDesiredRoommatePreferences())
                .maxBudget(dto.getMaxBudget())
                .build();

        ad = adRepository.save(ad);

        if (dto.getImages() != null) {
            Ad finalAd = ad;
            List<AdImage> images = dto.getImages().stream()
                    .map(url -> {
                        AdImage image = new AdImage();
                        image.setUrl(String.valueOf(url));
                        image.setAd(finalAd);
                        return image;
                    })
                    .collect(Collectors.toList());

            adImageRepository.saveAll(images);
            ad.setImages(images);
        }

        return mapToResponseDto(ad);
    }

    @Transactional
    public AdResponse getAdById(UUID adId) throws NotFoundException {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        if (ad.getStatus() == Status.DELETED || ad.getStatus() == Status.INACTIVE) {
            throw new NotFoundException("Ad is not available");
        }

        return mapToResponseDto(ad);
    }

    @Transactional
    public AdResponse updateAd(UUID adId, AdRequest dto, UUID currentUserId) throws NotFoundException, AccessDeniedException, ValidationException {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        if (!ad.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User not authorized to update this ad");
        }

        validateAdRequest(dto);

        City city = cityRepository.findById(dto.getCity())
                .orElseThrow(() -> new NotFoundException("City not found"));

        ad.setTitle(dto.getTitle());
        ad.setDescription(dto.getDescription());
        ad.setPrice(dto.getPrice());
        ad.setAddress(dto.getAddress());
        ad.setCity(city);
        ad.setType(AdType.valueOf(String.valueOf(dto.getType())));
        ad.setNumberOfRooms(dto.getNumberOfRooms());
        ad.setArea(dto.getArea());
        ad.setFloor(dto.getFloor());
        ad.setFurnished(dto.getFurnished());
        ad.setRoommatePreferences(dto.getRoommatePreferences());
        ad.setIsSharedRoom(dto.getIsSharedRoom());
        ad.setSeekerDescription(dto.getSeekerDescription());
        ad.setDesiredLocation(dto.getDesiredLocation());
        ad.setDesiredRoommatePreferences(dto.getDesiredRoommatePreferences());
        ad.setMaxBudget(dto.getMaxBudget());

        if (dto.getImages() != null) {
            if (ad.getImages() != null) {
                adImageRepository.deleteAll(ad.getImages());
            }
            Ad finalAd = ad;
            List<AdImage> newImages = dto.getImages().stream()
                    .map(url -> {
                        AdImage image = new AdImage();
                        image.setUrl(String.valueOf(url));
                        image.setAd(finalAd);
                        return image;
                    })
                    .collect(Collectors.toList());

            adImageRepository.saveAll(newImages);
            ad.setImages(newImages);
        }

        ad = adRepository.save(ad);

        return mapToResponseDto(ad);
    }

    @Transactional
    public void deleteAd(UUID adId, UUID currentUserId) throws AccessDeniedException, NotFoundException {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        if (!ad.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User not authorized to delete this ad");
        }

        ad.setStatus(Status.DELETED);
        adRepository.save(ad);
    }

    private void validateAdRequest(AdRequest dto) throws ValidationException {
        if (dto.getTitle() == null || dto.getTitle().isBlank())
            throw new ValidationException("Title is required");
        if (dto.getDescription() == null || dto.getDescription().isBlank())
            throw new ValidationException("Description is required");
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Price must be greater than zero");
        if (dto.getCity() == null)
            throw new ValidationException("City ID is required");
        if (dto.getType() == null)
            throw new ValidationException("Ad type is required");
    }

    private static AdResponse mapToResponseDto(Ad ad) {
        return AdResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .price(ad.getPrice())
                .address(ad.getAddress())
                .city(CityResponse.builder()
                        .id(ad.getCity().getId())
                        .name(ad.getCity().getName())
                        .build())
                .user(UserResponse.builder()
                        .id(ad.getUser().getId())
                        .fullName(ad.getUser().getName() + " " + ad.getUser().getSurname())
                        .email(ad.getUser().getEmail())
                        .phone(ad.getUser().getPhone())
                        .role(ad.getUser().getRole().name())
                        .build())
                .type(ad.getType())
                .status(ad.getStatus())
                .imageUrls(ad.getImages() != null
                        ? ad.getImages().stream()
                        .map(img -> AdImageResponse.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .adId(ad.getId())
                                .build())
                        .collect(Collectors.toList())
                        : List.of())
                .numberOfRooms(ad.getNumberOfRooms())
                .area(ad.getArea())
                .floor(ad.getFloor())
                .furnished(ad.getFurnished())
                .roommatePreferences(ad.getRoommatePreferences())
                .isSharedRoom(ad.getIsSharedRoom())
                .seekerDescription(ad.getSeekerDescription())
                .desiredLocation(ad.getDesiredLocation())
                .desiredRoommatePreferences(ad.getDesiredRoommatePreferences())
                .maxBudget(ad.getMaxBudget())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .build();
    }

}
