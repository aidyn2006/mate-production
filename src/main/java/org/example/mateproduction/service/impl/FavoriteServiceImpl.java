package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.dto.request.FavoriteRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.FavoriteResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.*;
import org.example.mateproduction.entity.contract.FavoriteHouseId;
import org.example.mateproduction.entity.contract.FavoriteSeekerId;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.*;
import org.example.mateproduction.service.FavoriteService;
import org.example.mateproduction.util.Type;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteHouseRepository favoriteHouseRepository; // Specific repo
    private final FavoriteSeekerRepository favoriteSeekerRepository; // Specific repo
    private final UserRepository userRepository;
    private final AdHouseRepository adHouseRepository; // Renamed for clarity
    private final AdSeekerRepository adSeekerRepository; // New repo

    @Override
    @Transactional
    public FavoriteResponse addFavorite(FavoriteRequest request) throws NotFoundException {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (request.getType() == Type.HOUSE) {
            AdHouse adHouse = adHouseRepository.findById(request.getAdId()).orElseThrow(() -> new NotFoundException("AdHouse not found"));
            FavoriteHouseId favoriteHouseId = new FavoriteHouseId(user.getId(), adHouse.getId());

            FavoriteHouse favoriteHouse = favoriteHouseRepository.findById(favoriteHouseId).orElseGet(() -> {
                FavoriteHouse newFavoriteHouse = FavoriteHouse.builder().id(favoriteHouseId).user(user).ad(adHouse) // 'ad' in FavoriteHouse refers to AdHouse
                        .createdAt(LocalDateTime.now()).build();
                return favoriteHouseRepository.save(newFavoriteHouse);
            });
            return mapHouseToResponseDto(favoriteHouse);
        } else if (request.getType() == Type.SEEKER) {
            AdSeeker adSeeker = adSeekerRepository.findById(request.getAdId()).orElseThrow(() -> new NotFoundException("AdSeeker not found"));
            FavoriteSeekerId favoriteSeekerId = new FavoriteSeekerId(user.getId(), adSeeker.getId());

            FavoriteSeeker favoriteSeeker = favoriteSeekerRepository.findById(favoriteSeekerId).orElseGet(() -> {
                FavoriteSeeker newFavoriteSeeker = FavoriteSeeker.builder().id(favoriteSeekerId).user(user).ad(adSeeker) // 'ad' in FavoriteSeeker refers to AdSeeker
                        .createdAt(LocalDateTime.now()).build();
                return favoriteSeekerRepository.save(newFavoriteSeeker);
            });
            return mapSeekerToResponseDto(favoriteSeeker);
        } else {
            throw new IllegalArgumentException("Invalid ad type specified: " + request.getType());
        }
    }


    @Override
    @Transactional
    public void removeFavorite(FavoriteRequest request) throws NotFoundException {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (request.getType() == Type.HOUSE) {
            FavoriteHouseId favoriteHouseId = new FavoriteHouseId(user.getId(), request.getAdId());
            if (!favoriteHouseRepository.existsById(favoriteHouseId)) {
                throw new NotFoundException("Favorite House Ad not found");
            }
            favoriteHouseRepository.deleteById(favoriteHouseId);
        } else if (request.getType() == Type.SEEKER) {
            FavoriteSeekerId favoriteSeekerId = new FavoriteSeekerId(user.getId(), request.getAdId());
            if (!favoriteSeekerRepository.existsById(favoriteSeekerId)) {
                throw new NotFoundException("Favorite Seeker Ad not found");
            }
            favoriteSeekerRepository.deleteById(favoriteSeekerId);
        } else {
            throw new IllegalArgumentException("Invalid ad type specified: " + request.getType());
        }
    }

    @Override
    public List<FavoriteResponse> getFavoritesByUser(UUID userId) {
        List<FavoriteResponse> allFavorites = new ArrayList<>();

        // Get favorite house ads
        List<FavoriteHouse> favoriteHouses = favoriteHouseRepository.findAllByUserId(userId);
        favoriteHouses.stream().map(this::mapHouseToResponseDto).forEach(allFavorites::add);

        // Get favorite seeker ads
        List<FavoriteSeeker> favoriteSeekers = favoriteSeekerRepository.findAllByUserId(userId);
        favoriteSeekers.stream().map(this::mapSeekerToResponseDto).forEach(allFavorites::add);

        return allFavorites;
    }


    @Override
    public boolean isFavorite(FavoriteRequest request) throws NotFoundException {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (request.getType() == Type.HOUSE) {
            return favoriteHouseRepository.existsById(new FavoriteHouseId(user.getId(), request.getAdId()));
        } else if (request.getType() == Type.SEEKER) {
            return favoriteSeekerRepository.existsById(new FavoriteSeekerId(user.getId(), request.getAdId()));
        } else {
            throw new IllegalArgumentException("Invalid ad type specified: " + request.getType());
        }
    }

    private UUID getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new SecurityException("User is not authenticated");
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }

        throw new SecurityException("Invalid user principal");
    }

    @Override
    public List<UUID> getFavoritedHouseAdIds(UUID userId) {
        return favoriteHouseRepository.findAllByUserId(userId).stream()
                .map(favoriteHouse -> favoriteHouse.getId().getAdHouseId())
                .collect(Collectors.toList());
    }

    // --- Mapping Methods ---

    private UserResponse mapUserToResponseDto(User user) {
        return UserResponse.builder().id(user.getId()).name(user.getName()).surname(user.getSurname()).username(user.getUsername()).email(user.getEmail()).phone(user.getPhone()).role(user.getRole()).isVerified(user.getIsVerified()).avatarUrl(user.getAvatarUrl()).token(null) // Token should not be exposed here
                .isDeleted(user.getIsDeleted()).build();
    }

    private AdHouseResponse mapAdHouseToResponseDto(AdHouse ad) {
        User adUser = ad.getUser();
        UserResponse adUserResponse = mapUserToResponseDto(adUser); // Reuse user mapping

        return AdHouseResponse.builder().id(ad.getId()).title(ad.getTitle()).description(ad.getDescription()).price(ad.getPrice()).address(ad.getAddress()).city(ad.getCity()).user(adUserResponse).type(ad.getType()).status(ad.getStatus()).images(ad.getImages()).numberOfRooms(ad.getNumberOfRooms()).area(ad.getArea()).floor(ad.getFloor()).furnished(ad.getFurnished()).contactPhoneNumber(ad.getContactPhoneNumber()).views(ad.getViews()).typeOfAd(ad.getTypeOfAd()).createdAt(ad.getCreatedAt()).updatedAt(ad.getUpdatedAt()).build();
    }

    private AdSeekerResponse mapAdSeekerToResponseDto(AdSeeker ad) {
        User adUser = ad.getUser();
        UserResponse adUserResponse = mapUserToResponseDto(adUser); // Reuse user mapping

        return AdSeekerResponse.builder().id(ad.getId()).age(ad.getAge()).gender(ad.getGender()).user(adUserResponse).seekerDescription(ad.getSeekerDescription()).city(ad.getCity()).desiredLocation(ad.getDesiredLocation()).maxBudget(ad.getMaxBudget()).moveInDate(ad.getMoveInDate()).hasFurnishedPreference(ad.getHasFurnishedPreference()).roommatePreferences(ad.getRoommatePreferences()).status(ad.getStatus()).views(ad.getViews()).typeOfAd(ad.getTypeOfAd()).contactPhoneNumber(ad.getContactPhoneNumber()).createdAt(ad.getCreatedAt()).updatedAt(ad.getUpdatedAt()).build();
    }


    private FavoriteResponse mapHouseToResponseDto(FavoriteHouse favoriteHouse) {
        return FavoriteResponse.builder().user(mapUserToResponseDto(favoriteHouse.getUser())).type(Type.HOUSE) // Set the type
                .adHouse(mapAdHouseToResponseDto(favoriteHouse.getAd())) // Map AdHouse
                .adSeeker(null) // Ensure AdSeeker is null
                .createdAt(favoriteHouse.getCreatedAt()).build();
    }

    private FavoriteResponse mapSeekerToResponseDto(FavoriteSeeker favoriteSeeker) {
        return FavoriteResponse.builder().user(mapUserToResponseDto(favoriteSeeker.getUser())).type(Type.SEEKER) // Set the type
                .adHouse(null) // Ensure AdHouse is null
                .adSeeker(mapAdSeekerToResponseDto(favoriteSeeker.getAd())) // Map AdSeeker
                .createdAt(favoriteSeeker.getCreatedAt()).build();
    }
}