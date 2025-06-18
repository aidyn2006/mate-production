package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.AdHouseFilter;
import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface AdHouseService {

    AdHouseResponse createAd(AdHouseRequest dto) throws ValidationException, NotFoundException;

    AdHouseResponse getAdById(UUID adId) throws NotFoundException;

    AdHouseResponse updateAd(UUID adId, AdHouseRequest dto) throws NotFoundException, AccessDeniedException, ValidationException;

    void deleteAd(UUID adId) throws AccessDeniedException, NotFoundException;

    List<AdHouseResponse> getAllAds();
    List<AdHouseResponse> findByFilter(AdHouseFilter filter);

}
