package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
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
    public Favorite addFavorite(UUID adId) throws NotFoundException {
        UUID userId=getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        AdHouse ad = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        FavoriteId favoriteId = new FavoriteId(user.getId(), ad.getId());
        if (favoriteRepository.existsById(favoriteId)) {
            return favoriteRepository.findById(favoriteId).get();
        }

        Favorite favorite = Favorite.builder()
                .id(favoriteId) // <-- добавляем ID
                .user(user)
                .ad(ad)
                .createdAt(new Date())
                .build();


        return favoriteRepository.save(favorite);
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
    public List<Favorite> getFavoritesByUser(UUID userId) {
        return favoriteRepository.findAllByUserId(userId);
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
}
