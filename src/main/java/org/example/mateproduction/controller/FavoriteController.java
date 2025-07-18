package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.dto.request.FavoriteRequest;
import org.example.mateproduction.dto.response.FavoriteResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(@RequestBody FavoriteRequest request) throws NotFoundException {
        FavoriteResponse favorite = favoriteService.addFavorite(request);
        return new ResponseEntity<>(favorite, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(@RequestBody FavoriteRequest request) throws NotFoundException {
        favoriteService.removeFavorite(request);
        return ResponseEntity.noContent().build(); // 204 No Content for successful deletion
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteResponse>> getFavoritesByUser(
            @PathVariable UUID userId) {
        List<FavoriteResponse> favorites = favoriteService.getFavoritesByUser(userId);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> isFavorite(@RequestBody FavoriteRequest request) throws NotFoundException {
        boolean result = favoriteService.isFavorite(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<UUID>> getUserFavorites() { // Remove Principal from params
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof JwtUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUser().getId();

        List<UUID> favoritedIds = favoriteService.getFavoritedHouseAdIds(userId);
        return ResponseEntity.ok(favoritedIds);
    }

    /*
    // Alternative for isFavorite if you prefer GET and Query Params:
    @GetMapping("/check")
    public ResponseEntity<Boolean> isFavorite(
            @RequestParam UUID adId,
            @RequestParam Type type) throws NotFoundException {
        FavoriteRequest request = FavoriteRequest.builder().adId(adId).type(type).build();
        boolean result = favoriteService.isFavorite(request);
        return ResponseEntity.ok(result);
    }
    */
}