package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.FavoriteResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.Favorite;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.entity.contract.FavoriteId;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.AdHouseRepository;
import org.example.mateproduction.repository.FavoriteRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.FavoriteService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final AdHouseRepository adRepository;

    @Override
    @Transactional
    public FavoriteResponse addFavorite(UUID adId) throws NotFoundException {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        AdHouse ad = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        FavoriteId favoriteId = new FavoriteId(user.getId(), ad.getId());

        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseGet(() -> {
                    Favorite newFavorite = Favorite.builder()
                            .id(favoriteId)
                            .user(user)
                            .ad(ad)
                            .createdAt(new Date())
                            .build();
                    return favoriteRepository.save(newFavorite);
                });

        return mapToResponseDto(favorite);
    }


    @Override
    @Transactional
    public void removeFavorite(UUID adId) throws NotFoundException {
        UUID userId=getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        AdHouse ad = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        FavoriteId favoriteId = new FavoriteId(user.getId(), ad.getId());
        if (!favoriteRepository.existsById(favoriteId)) {
            throw new NotFoundException("Favorite not found");
        }

        favoriteRepository.deleteById(favoriteId);
    }

    @Override
    public List<FavoriteResponse> getFavoritesByUser(UUID userId) {
        List<Favorite> favorites = favoriteRepository.findAllByUserId(userId);
        return favorites.stream()
                .map(this::mapToResponseDto)
                .toList();
    }


    @Override
    public boolean isFavorite(UUID adId) throws NotFoundException {
        UUID userId=getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        AdHouse ad = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        return favoriteRepository.existsById(new FavoriteId(user.getId(), ad.getId()));
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

    private FavoriteResponse mapToResponseDto(Favorite favorite) {
        User user = favorite.getUser();
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isVerified(user.getIsVerified())
                .avatarUrl(user.getAvatarUrl())
                .token(null)
                .isDeleted(user.getIsDeleted())
                .build();

        AdHouse ad = favorite.getAd();
        User adUser = ad.getUser();
        UserResponse adUserResponse = UserResponse.builder()
                .id(adUser.getId())
                .name(adUser.getName())
                .surname(adUser.getSurname())
                .username(adUser.getUsername())
                .email(adUser.getEmail())
                .phone(adUser.getPhone())
                .role(adUser.getRole())
                .isVerified(adUser.getIsVerified())
                .avatarUrl(adUser.getAvatarUrl())
                .token(null)
                .isDeleted(adUser.getIsDeleted())
                .build();

        AdHouseResponse adHouseResponse = AdHouseResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .price(ad.getPrice())
                .address(ad.getAddress())
                .city(ad.getCity())
                .user(adUserResponse)
                .type(ad.getType())
                .status(ad.getStatus())
                .images(ad.getImages())
                .numberOfRooms(ad.getNumberOfRooms())
                .area(ad.getArea())
                .floor(ad.getFloor())
                .furnished(ad.getFurnished())
                .contactPhoneNumber(ad.getContactPhoneNumber())
                .views(ad.getViews())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .build();

        return FavoriteResponse.builder()
                .user(userResponse)
                .ad(adHouseResponse)
                .createdAt(favorite.getCreatedAt())
                .build();
    }

}
