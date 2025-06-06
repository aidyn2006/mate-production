package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.CityRequest;
import org.example.mateproduction.dto.response.CityResponse;
import org.example.mateproduction.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface CityService {
    CityResponse createCity(CityRequest request);
    CityResponse getCityById(UUID id) throws NotFoundException;
    List<CityResponse> getAllCities();
    CityResponse updateCity(UUID id, CityRequest request) throws NotFoundException;
    void deleteCity(UUID id) throws NotFoundException;
}
