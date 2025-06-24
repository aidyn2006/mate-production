package org.example.mateproduction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.AdSeekerRequest;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.AdminReasonResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.service.AdminSeekerAdService;
import org.example.mateproduction.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/seeker-ads")
@RequiredArgsConstructor
public class AdminSeekerAdController {

    private final AdminSeekerAdService adminSeekerAdService;

    @GetMapping("/moderation")
    public ResponseEntity<Page<AdSeekerResponse>> getAllUnderModeration(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminSeekerAdService.getAllModerateAds(page, size));
    }

    // Утвердить объявление
    @PostMapping("/{adId}/approve")
    public ResponseEntity<Void> approveAd(@PathVariable UUID adId) {
        adminSeekerAdService.approveAd(adId);
        return ResponseEntity.noContent().build();
    }

    // Отклонить объявление с причиной
    @PostMapping("/{adId}/reject")
    public ResponseEntity<AdminReasonResponse> rejectAd(@PathVariable UUID adId, @RequestParam String reason) {
        return ResponseEntity.ok(adminSeekerAdService.rejectAd(adId, reason));
    }

    // Изменить статус (ACTIVE, REJECTED, MODERATION)
    @PutMapping("/{adId}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable UUID adId, @RequestParam Status newStatus, @RequestParam(required = false) String reason) {
        adminSeekerAdService.changeAdStatus(adId, newStatus, reason);
        return ResponseEntity.noContent().build();
    }

    // Обновить объявление
    @PutMapping("/{adId}")
    public ResponseEntity<AdSeekerResponse> updateAd(@PathVariable UUID adId, @Valid @RequestBody AdSeekerRequest request) throws NotFoundException, AccessDeniedException, ValidationException {
        return ResponseEntity.ok(adminSeekerAdService.updateAd(adId, request));
    }
}
