package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.CityRequest;
import org.example.mateproduction.dto.response.CityResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @PostMapping
    public ResponseEntity<CityResponse> create(@RequestBody CityRequest request) {
        CityResponse response = cityService.createCity(request);
        return ResponseEntity
                .created(URI.create("/api/v1/cities/" + response.getId()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityResponse> getById(@PathVariable UUID id) throws NotFoundException {
        return ResponseEntity.ok(cityService.getCityById(id));
    }

    @GetMapping
    public ResponseEntity<List<CityResponse>> getAll() {
        return ResponseEntity.ok(cityService.getAllCities());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CityResponse> update(@PathVariable UUID id,
                                               @RequestBody CityRequest request) throws NotFoundException {
        return ResponseEntity.ok(cityService.updateCity(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws NotFoundException {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }
}
