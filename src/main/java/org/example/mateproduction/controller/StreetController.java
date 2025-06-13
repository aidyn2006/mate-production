package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.StreetRequest;
import org.example.mateproduction.dto.response.StreetResponse;
import org.example.mateproduction.service.StreetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/street")
@RequiredArgsConstructor
public class StreetController {

    private final StreetService streetService;

    @PostMapping
    public StreetResponse create(@RequestBody StreetRequest request) {
        return streetService.create(request);
    }

    @PutMapping("/{id}")
    public StreetResponse update(@PathVariable UUID id, @RequestBody StreetRequest request) {
        return streetService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        streetService.delete(id);
    }

    @GetMapping("/{id}")
    public StreetResponse getById(@PathVariable UUID id) {
        return streetService.getById(id);
    }

    @GetMapping
    public List<StreetResponse> getAll() {
        return streetService.getAll();
    }
}
