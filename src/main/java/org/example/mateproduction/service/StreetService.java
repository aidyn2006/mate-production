package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.StreetRequest;
import org.example.mateproduction.dto.response.StreetResponse;

import java.util.List;
import java.util.UUID;

public interface StreetService {
    StreetResponse create(StreetRequest request);
    StreetResponse update(UUID id, StreetRequest request);
    void delete(UUID id);
    StreetResponse getById(UUID id);
    List<StreetResponse> getAll();
}
