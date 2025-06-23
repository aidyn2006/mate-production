package org.example.mateproduction.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.mateproduction.dto.request.AdHouseFilter;
import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface AdHouseService {

    AdHouseResponse createAd(AdHouseRequest dto) throws ValidationException, NotFoundException;

    AdHouseResponse getAdById(UUID adId, HttpServletRequest request) throws NotFoundException;
    AdHouseResponse updateAd(UUID adId, AdHouseRequest dto) throws NotFoundException, AccessDeniedException, ValidationException;

    void deleteAd(UUID adId) throws AccessDeniedException, NotFoundException;

<<<<<<< HEAD
    Page<AdHouseResponse> getAllAds(int page, int size);
    Page<AdHouseResponse> findByFilter(AdHouseFilter filter, int page, int size);
    void updateMainImage(UUID adId, String mainImageUrl) throws NotFoundException, AccessDeniedException, ValidationException;


=======
    Page<AdHouseResponse> searchAds(AdHouseFilter filter, Pageable pageable);
>>>>>>> 32350c647ad863a9eb19c59e5be942fc327063cd

}
