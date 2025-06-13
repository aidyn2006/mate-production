package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.DistrictRequest;
import org.example.mateproduction.dto.response.DistrictResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.DistrictService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/districts")
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @PostMapping
    public ResponseEntity<DistrictResponse> create(@RequestBody DistrictRequest request) throws NotFoundException {
        DistrictResponse response = districtService.createDistrict(request);
        return ResponseEntity.created(URI.create("/api/v1/districts/" + response.getId())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DistrictResponse> getById(@PathVariable UUID id) throws NotFoundException {
        return ResponseEntity.ok(districtService.getDistrictById(id));
    }

    @GetMapping
    public ResponseEntity<List<DistrictResponse>> getAll() {
        return ResponseEntity.ok(districtService.getAllDistricts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DistrictResponse> update(@PathVariable UUID id,
                                                   @RequestBody DistrictRequest request) throws NotFoundException {
        return ResponseEntity.ok(districtService.updateDistrict(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws NotFoundException {
        districtService.deleteDistrict(id);
        return ResponseEntity.noContent().build();
    }
}
