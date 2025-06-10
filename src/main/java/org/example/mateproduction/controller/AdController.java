package org.example.mateproduction.controller;


import org.example.mateproduction.dto.request.AdRequest;
import org.example.mateproduction.dto.response.AdResponse;
import org.example.mateproduction.entity.Ad;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/ads")
public class AdController {

    private final AdService adService;

    @Autowired
    public AdController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping
    public ResponseEntity<List<AdResponse>> getAds() {
        return ResponseEntity.ok(adService.getAllAds());
    }

    @PostMapping
    public ResponseEntity<AdResponse> createAd(
            @RequestBody AdRequest adRequest,
            @RequestParam UUID userId
    ) throws ValidationException, NotFoundException {
        return new ResponseEntity<>(adService.createAd(adRequest, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{adId}")
    public ResponseEntity<AdResponse> updateAd(
            @PathVariable UUID adId,
            @RequestBody AdRequest adRequest,
            @RequestParam UUID userId
    ) throws ValidationException, NotFoundException, AccessDeniedException {
        return ResponseEntity.ok(adService.updateAd(adId, adRequest, userId));
    }

    @DeleteMapping("/{adId}")
    public ResponseEntity<Void> deleteAd(
            @PathVariable UUID adId,
            @RequestParam UUID userId
    ) throws NotFoundException, AccessDeniedException {
        adService.deleteAd(adId, userId);
        return ResponseEntity.noContent().build();
    }
}

