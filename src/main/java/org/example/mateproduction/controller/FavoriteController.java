package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<Favorite> addFavorite(@RequestParam UUID userId, @RequestParam UUID adId) {
        Favorite favorite = favoriteService.addFavorite(userId, adId);
        return ResponseEntity.ok(favorite);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(@RequestParam UUID userId, @RequestParam UUID adId) throws NotFoundException {
        favoriteService.removeFavorite(userId, adId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> getFavoritesByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(favoriteService.getFavoritesByUser(userId));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> isFavorite(@RequestParam UUID userId, @RequestParam UUID adId) {
        return ResponseEntity.ok(favoriteService.isFavorite(userId, adId));
    }
}
