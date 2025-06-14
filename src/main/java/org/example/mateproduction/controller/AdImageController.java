package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/img")
@RequiredArgsConstructor
public class AdImageController {
    private final AdImageService adImageService;

    @PostMapping
    public ResponseEntity<AdImageResponse> save(@ModelAttribute AdImageRequest adImage) throws NotFoundException {
        AdImageResponse response = adImageService.createAdImage(adImage);
        return ResponseEntity
                .created(URI.create("/api/v1/img/" + response.getId()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdImageResponse> getById(@PathVariable UUID id) throws NotFoundException {
        return ResponseEntity.ok(adImageService.getAdImageById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdImageResponse> update(@PathVariable UUID id, @RequestBody AdImageRequest request) throws NotFoundException {
        return ResponseEntity.ok(adImageService.updateAdImage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws NotFoundException {
        adImageService.deleteAdImage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AdImageResponse>> getAll() {
        return ResponseEntity.ok(adImageService.getAllAdImages());
    }
}
