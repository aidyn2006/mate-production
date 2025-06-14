package org.example.mateproduction.controller;

import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.service.AdHouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/ads")
public class AdHouseController {

    private final AdHouseService adService;

    public AdHouseController(AdHouseService adService) {
        this.adService = adService;
    }

    @GetMapping
    public ResponseEntity<List<AdHouseResponse>> getAds() {
        List<AdHouseResponse> ads = adService.getAllAds();
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/{adId}")
    public ResponseEntity<AdHouseResponse> getAdById(@PathVariable UUID adId) throws NotFoundException {
        return ResponseEntity.ok(adService.getAdById(adId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdHouseResponse createAd(@RequestBody AdHouseRequest adRequest) throws ValidationException, NotFoundException {
        return adService.createAd(adRequest);
    }

    @PutMapping("/{adId}")
    public ResponseEntity<AdHouseResponse> updateAd(
            @PathVariable UUID adId,
            @RequestBody AdHouseRequest adRequest
    ) throws ValidationException, NotFoundException, AccessDeniedException {
        AdHouseResponse updatedAd = adService.updateAd(adId, adRequest);
        return ResponseEntity.ok(updatedAd);
    }

    @DeleteMapping("/{adId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAd(@PathVariable UUID adId) throws NotFoundException, AccessDeniedException {
        adService.deleteAd(adId);
    }
}
