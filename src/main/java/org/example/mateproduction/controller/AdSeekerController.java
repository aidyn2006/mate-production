package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.AdHouseFilter;
import org.example.mateproduction.dto.request.AdSeekerFilter;
import org.example.mateproduction.dto.request.AdSeekerRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.service.AdSeekerService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ads/seekers")
@RequiredArgsConstructor
public class AdSeekerController {

    private final AdSeekerService adSeekerService;

    @GetMapping
    public ResponseEntity<Page<AdSeekerResponse>> getAllAds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adSeekerService.getAllAds(page, size));
    }


    @GetMapping("/{id}")
    public ResponseEntity<AdSeekerResponse> getAdById(@PathVariable UUID id) throws NotFoundException {
        return ResponseEntity.ok(adSeekerService.getAdById(id));
    }

    @PostMapping
    public ResponseEntity<AdSeekerResponse> createAd(@RequestBody AdSeekerRequest request)
            throws ValidationException, NotFoundException {
        return ResponseEntity.ok(adSeekerService.createAd(request));
    }
    @PostMapping("/filter")
    public ResponseEntity<Page<AdSeekerResponse>> filterAd(
            @RequestBody AdSeekerFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adSeekerService.findByFilter(filter, page, size));
    }



    @PutMapping("/{id}")
    public ResponseEntity<AdSeekerResponse> updateAd(@PathVariable UUID id, @RequestBody AdSeekerRequest request)
            throws NotFoundException, AccessDeniedException, ValidationException {
        return ResponseEntity.ok(adSeekerService.updateAd(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable UUID id)
            throws NotFoundException, AccessDeniedException {
        adSeekerService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }
}
