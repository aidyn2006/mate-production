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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ads/houses")
@RequiredArgsConstructor
public class AdHouseController implements AdHouseControllerApi {

    private final AdHouseService adHouseService;

    @GetMapping
    public ResponseEntity<Page<AdHouseResponse>> getAllAds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AdHouseResponse> result = adHouseService.getAllAds(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdHouseResponse> getAdById(
            @PathVariable UUID id,
            HttpServletRequest request
    ) throws NotFoundException {
        AdHouseResponse ad = adHouseService.getAdById(id, request);
        return ResponseEntity.ok(ad);
    }


    @PostMapping
    public ResponseEntity<AdHouseResponse>  createAd(
            @ModelAttribute AdHouseRequest request
    ) throws ValidationException, NotFoundException {
        AdHouseResponse createdAd = adHouseService.createAd(request);
        return ResponseEntity.ok(createdAd);
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<AdHouseResponse>> filterAd(
            @RequestBody AdHouseFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adHouseService.findByFilter(filter, page, size));
    }


    @PutMapping("/{id}")
    public ResponseEntity<AdHouseResponse> updateAd(
            @PathVariable UUID id,
            @ModelAttribute AdHouseRequest request // <--- Still uses @ModelAttribute
    ) throws NotFoundException, AccessDeniedException, ValidationException {
        AdHouseResponse updatedAd = adHouseService.updateAd(id, request);
        return ResponseEntity.ok(updatedAd);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable UUID id) throws AccessDeniedException, NotFoundException {
        adHouseService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}/status")
    public ResponseEntity<AdHouseResponse> updateAdStatus() {
        return null;
    }


    @PutMapping("/{id}/main-image")
    public ResponseEntity<Void> updateMainImage(@PathVariable UUID id, @RequestParam String mainImageUrl
    ) throws NotFoundException, AccessDeniedException, ValidationException {
        adHouseService.updateMainImage(id, mainImageUrl);
        return ResponseEntity.ok().build();
    }


}
