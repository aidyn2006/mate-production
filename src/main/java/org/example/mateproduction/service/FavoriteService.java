package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.FavoriteRequest; // Use unified request
import org.example.mateproduction.dto.response.FavoriteResponse;
import org.example.mateproduction.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface FavoriteService {
    FavoriteResponse addFavorite(FavoriteRequest request) throws NotFoundException; // Takes unified request
    void removeFavorite(FavoriteRequest request) throws NotFoundException; // Takes unified request
    List<FavoriteResponse> getFavoritesByUser(UUID userId);
    boolean isFavorite(FavoriteRequest request) throws NotFoundException; // Takes unified request
}