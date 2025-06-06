package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import org.example.mateproduction.entity.Favorite;
import org.example.mateproduction.entity.contract.FavoriteId;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.FavoriteRepository;
import org.example.mateproduction.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    @Transactional
    public Favorite addFavorite(UUID userId, UUID adId) {
        FavoriteId favoriteId = new FavoriteId(userId, adId);
        if (favoriteRepository.existsById(favoriteId)) {
            return favoriteRepository.findById(favoriteId).get();
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setAdId(adId);
        favorite.setCreatedAt(new Date());

        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(UUID userId, UUID adId) throws NotFoundException {
        FavoriteId favoriteId = new FavoriteId(userId, adId);
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
    public boolean isFavorite(UUID userId, UUID adId) {
        FavoriteId favoriteId = new FavoriteId(userId, adId);
        return favoriteRepository.existsById(favoriteId);
    }
}
