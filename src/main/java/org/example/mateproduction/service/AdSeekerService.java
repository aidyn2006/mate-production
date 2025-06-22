package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.AdSeekerFilter;
import org.example.mateproduction.dto.request.AdSeekerRequest;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.springframework.data.domain.Page;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface AdSeekerService {

    Page<AdSeekerResponse> getAllAds(int page, int size);

    AdSeekerResponse getAdById(UUID adId) throws NotFoundException;

    AdSeekerResponse createAd(AdSeekerRequest dto) throws ValidationException, NotFoundException;

    AdSeekerResponse updateAd(UUID adId, AdSeekerRequest dto) throws NotFoundException, AccessDeniedException, ValidationException;

    void deleteAd(UUID adId) throws NotFoundException, AccessDeniedException;
    Page<AdSeekerResponse> findByFilter(AdSeekerFilter filter, int page, int size);
}
