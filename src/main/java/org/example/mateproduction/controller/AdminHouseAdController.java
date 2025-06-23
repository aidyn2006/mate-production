package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.AdHouseFilter;
import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdminReasonResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.service.AdminHouseAdService;
import org.example.mateproduction.util.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/listings/houses")
public class AdminHouseAdController {

    private final AdminHouseAdService adminHouseAdService;

    @GetMapping
    public ResponseEntity<?> getAllModerateAds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminHouseAdService.getAllModerateAds(page, size));
    }

    @PostMapping("/filter")
    public ResponseEntity<?> findByFilter(
            @RequestBody AdHouseFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminHouseAdService.findByFilter(filter, page, size));
    }

    @PutMapping("/{adId}/approve")
    public ResponseEntity<Void> approveAd(@PathVariable UUID adId) {
        adminHouseAdService.approveAd(adId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{adId}/reject")
    public ResponseEntity<AdminReasonResponse> rejectAd(
            @PathVariable UUID adId,
            @RequestParam String reason
    ) {
        return ResponseEntity.ok(adminHouseAdService.rejectAd(adId, reason));
    }

    @PutMapping("/{adId}")
    public ResponseEntity<AdHouseResponse> updateAd(
            @PathVariable UUID adId,
            @ModelAttribute AdHouseRequest request
    ) throws NotFoundException, AccessDeniedException, ValidationException {
        return ResponseEntity.ok(adminHouseAdService.updateAd(adId, request));
    }

    @PatchMapping("/{adId}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable UUID adId,
            @RequestParam Status status,
            @RequestParam(required = false) String reason
    ) {
        adminHouseAdService.changeAdStatus(adId, status, reason);
        return ResponseEntity.ok().build();
    }
}
