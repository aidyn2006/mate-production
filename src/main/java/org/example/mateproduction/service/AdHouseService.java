package org.example.mateproduction.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.mateproduction.dto.request.AdHouseFilter;
import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.springframework.data.domain.Page;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface AdHouseService {

    AdHouseResponse createAd(AdHouseRequest dto) throws ValidationException, NotFoundException;

    AdHouseResponse getAdById(UUID adId, HttpServletRequest request) throws NotFoundException;
    AdHouseResponse updateAd(UUID adId, AdHouseRequest dto) throws NotFoundException, AccessDeniedException, ValidationException;

    void deleteAd(UUID adId) throws AccessDeniedException, NotFoundException;

    Page<AdHouseResponse> getAllAds(int page, int size);
    Page<AdHouseResponse> findByFilter(AdHouseFilter filter, int page, int size);
    void updateMainImage(UUID adId, String mainImageUrl) throws NotFoundException, AccessDeniedException, ValidationException;



}
