package org.example.mateproduction.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.AdSeekerFilter;
import org.example.mateproduction.dto.request.AdSeekerRequest;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.service.AdSeekerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ads/seekers")
@RequiredArgsConstructor
public class AdSeekerController {

    private final AdSeekerService adSeekerService;

    @GetMapping
    public ResponseEntity<Page<AdSeekerResponse>> getAllAds(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adSeekerService.getAllAds(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AdSeekerResponse>> searchSeekers(@ModelAttribute AdSeekerFilter filter, // Add @ModelAttribute here
                                                                @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdSeekerResponse> results = adSeekerService.searchAds(filter, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdSeekerResponse> getAdById(@PathVariable UUID id, HttpServletRequest request) throws NotFoundException {
        return ResponseEntity.ok(adSeekerService.getAdById(id,request));
    }

    @PostMapping
    public ResponseEntity<AdSeekerResponse> createAd(@RequestBody AdSeekerRequest request) throws ValidationException, NotFoundException {
        return ResponseEntity.ok(adSeekerService.createAd(request));
    }

//    @PostMapping("/filter")
//    public ResponseEntity<Page<AdSeekerResponse>> filterAd(
//            @RequestBody AdSeekerFilter filter,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return ResponseEntity.ok(adSeekerService.findByFilter(filter, page, size));
//    }

    @PutMapping("/{id}")
    public ResponseEntity<AdSeekerResponse> updateAd(@PathVariable UUID id, @RequestBody AdSeekerRequest request) throws NotFoundException, AccessDeniedException, ValidationException {
        return ResponseEntity.ok(adSeekerService.updateAd(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable UUID id) throws NotFoundException, AccessDeniedException {
        adSeekerService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }
}
