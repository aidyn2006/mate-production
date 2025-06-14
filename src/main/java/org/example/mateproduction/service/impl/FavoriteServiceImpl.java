package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.Favorite;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.entity.contract.FavoriteId;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.AdRepository;
import org.example.mateproduction.repository.FavoriteRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final AdRepository adRepository;

    @Override
    @Transactional
    public Favorite addFavorite(UUID userId, UUID adId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        AdHouse ad = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        FavoriteId favoriteId = new FavoriteId(user.getId(), ad.getId());
        if (favoriteRepository.existsById(favoriteId)) {
            return favoriteRepository.findById(favoriteId).get();
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .ad(ad)
                .createdAt(new Date())
                .build();

        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(UUID userId, UUID adId) throws NotFoundException {
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
    public boolean isFavorite(UUID userId, UUID adId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        AdHouse ad = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        return favoriteRepository.existsById(new FavoriteId(user.getId(), ad.getId()));
    }
}
