package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.StreetRequest;
import org.example.mateproduction.dto.response.StreetResponse;

import java.util.List;
import java.util.UUID;

public interface StreetService {

    StreetResponse createStreet(StreetRequest request);
    List<StreetResponse> getAllStreet();

    StreetResponse getStreetByCity(UUID cityId);

    StreetResponse updateStreet(UUID streetId,StreetRequest request);
    void deleteStreet(UUID streetId);
}
