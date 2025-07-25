package org.example.mateproduction.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.controller.openapi.AdHouseControllerApi;
import org.example.mateproduction.dto.request.AdHouseFilter;
import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.service.AdHouseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ads/houses")
@RequiredArgsConstructor
public class AdHouseController implements AdHouseControllerApi {

    private final AdHouseService adHouseService;

    @GetMapping("/{id}")
    public ResponseEntity<AdHouseResponse> getAdById(@PathVariable UUID id, HttpServletRequest request) throws NotFoundException {
        AdHouseResponse ad = adHouseService.getAdById(id, request);
        return ResponseEntity.ok(ad);
    }


    @PostMapping
    public ResponseEntity<AdHouseResponse> createAd(@ModelAttribute AdHouseRequest request) throws ValidationException, NotFoundException {
        AdHouseResponse createdAd = adHouseService.createAd(request);
        return ResponseEntity.ok(createdAd);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<AdHouseResponse>> searchHouses(AdHouseFilter filter, @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdHouseResponse> results = adHouseService.searchAds(filter, pageable);
        return ResponseEntity.ok(results);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdHouseResponse> updateAd(@PathVariable UUID id, @ModelAttribute AdHouseRequest request) throws NotFoundException, AccessDeniedException, ValidationException {
        AdHouseResponse updatedAd = adHouseService.updateAd(id, request);
        return ResponseEntity.ok(updatedAd);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable UUID id) throws AccessDeniedException, NotFoundException {
        adHouseService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/main-image")
    public ResponseEntity<Void> updateMainImage(@PathVariable UUID id, @RequestParam String mainImageUrl) throws NotFoundException, AccessDeniedException, ValidationException {
        adHouseService.updateMainImage(id, mainImageUrl);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archiveAd(@PathVariable UUID id) throws NotFoundException, AccessDeniedException {
        adHouseService.archiveAd(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{id}/unarchive")
    public ResponseEntity<Void> unarchiveAd(@PathVariable UUID id) throws NotFoundException, AccessDeniedException {
        adHouseService.unarchiveAd(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
