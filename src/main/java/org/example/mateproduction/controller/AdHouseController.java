package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.service.AdHouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdHouseController {

    private final AdHouseService adHouseService;

    @GetMapping
    public ResponseEntity<List<AdHouseResponse>> getAllAds() {
        List<AdHouseResponse> ads = adHouseService.getAllAds();
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdHouseResponse> getAdById(@PathVariable UUID id) throws NotFoundException {
        AdHouseResponse ad = adHouseService.getAdById(id);
        return ResponseEntity.ok(ad);
    }

    @PostMapping
    public ResponseEntity<AdHouseResponse> createAd(
            @ModelAttribute AdHouseRequest request
    ) throws ValidationException, NotFoundException {
        AdHouseResponse createdAd = adHouseService.createAd(request);
        return ResponseEntity.ok(createdAd);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdHouseResponse> updateAd(
            @PathVariable UUID id,
            @ModelAttribute AdHouseRequest request
    ) throws NotFoundException, AccessDeniedException, ValidationException {
        AdHouseResponse updatedAd = adHouseService.updateAd(id, request);
        return ResponseEntity.ok(updatedAd);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable UUID id) throws AccessDeniedException, NotFoundException {
        adHouseService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }
}
