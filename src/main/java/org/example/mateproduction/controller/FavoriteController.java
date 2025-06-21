package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.response.FavoriteResponse;
import org.example.mateproduction.entity.Favorite;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{adId}")
    public ResponseEntity<FavoriteResponse> addFavorite(@PathVariable UUID adId) throws NotFoundException {
        FavoriteResponse favorite = favoriteService.addFavorite(adId);
        return ResponseEntity.ok(favorite);
    }

    @DeleteMapping("/{adId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable UUID adId) throws NotFoundException {
        favoriteService.removeFavorite(adId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteResponse>> getFavoritesByUser(
            @PathVariable UUID userId) {
        List<FavoriteResponse> favorites = favoriteService.getFavoritesByUser(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/check/{adId}")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable UUID adId) throws NotFoundException {
        boolean result = favoriteService.isFavorite(adId);
        return ResponseEntity.ok(result);
    }

}
