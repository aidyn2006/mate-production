package org.example.mateproduction.service;

import org.example.mateproduction.entity.Favorite;
import org.example.mateproduction.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface FavoriteService {
    Favorite addFavorite(UUID adId) throws NotFoundException;
    void removeFavorite(UUID adId) throws NotFoundException;
    List<Favorite> getFavoritesByUser(UUID userId);
    boolean isFavorite(UUID adId) throws NotFoundException;
}