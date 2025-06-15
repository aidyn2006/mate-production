package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.AdSeekerRequest;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.service.AdSeekerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/seeker-ads")
@RequiredArgsConstructor
public class AdSeekerController {

    private final AdSeekerService adSeekerService;

    @GetMapping
    public ResponseEntity<List<AdSeekerResponse>> getAllAds() {
        return ResponseEntity.ok(adSeekerService.getAllAds());
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
