package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.response.AdminListingDetailResponse;
import org.example.mateproduction.dto.response.AdminReasonResponse;
import org.example.mateproduction.factory.AdminServiceFactory;
import org.example.mateproduction.service.AdminListingService;
import org.example.mateproduction.util.Status;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/listings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminListingController {

    private final AdminServiceFactory serviceFactory;

    @GetMapping
    public ResponseEntity<?> getAllListings(
            @RequestParam(required = false) String listingType,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) UUID ownerUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        if (listingType != null && !listingType.isEmpty()) {
             return ResponseEntity.ok(serviceFactory.getService(listingType).findAll(ownerUserId, status, pageable));
        }

        return ResponseEntity.badRequest().body("listingType parameter is required.");
    }

    @GetMapping("/{type}/{adId}")
    public ResponseEntity<AdminListingDetailResponse> getListingDetails(
            @PathVariable String type,
            @PathVariable UUID adId) {
        AdminListingDetailResponse listing = serviceFactory.getService(type).findById(adId);
        return ResponseEntity.ok(listing);
    }
    
    @PutMapping("/{type}/{adId}/approve")
    public ResponseEntity<Void> approveAd(@PathVariable String type, @PathVariable UUID adId) {
        serviceFactory.getService(type).approveAd(adId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{type}/{adId}/reject")
    public ResponseEntity<AdminReasonResponse> rejectAd(@PathVariable String type, @PathVariable UUID adId, @RequestParam String reason) {
        return ResponseEntity.ok(serviceFactory.getService(type).rejectAd(adId, reason));
    }

    @DeleteMapping("/{type}/{adId}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable String type,
            @PathVariable UUID adId,
            @RequestParam(name = "mode", defaultValue = "soft") String deleteMode) { // <-- Add RequestParam

        AdminListingService<?> service = serviceFactory.getService(type);

        if ("hard".equalsIgnoreCase(deleteMode)) {
            service.hardDeleteAd(adId);
        } else {
            // Default to soft delete for safety
            service.softDeleteAd(adId);
        }

        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{type}/{adId}/feature")
    public ResponseEntity<Void> featureListing(@PathVariable String type, @PathVariable UUID adId) {
        serviceFactory.getService(type).featureAd(adId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{type}/{adId}/feature")
    public ResponseEntity<Void> unfeatureListing(@PathVariable String type, @PathVariable UUID adId) {
        serviceFactory.getService(type).unfeatureAd(adId);
        return ResponseEntity.noContent().build();
    }
}