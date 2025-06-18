package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.AdSeekerFilter;
import org.example.mateproduction.dto.request.AdSeekerRequest;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface AdSeekerService {

    List<AdSeekerResponse> getAllAds();

    AdSeekerResponse getAdById(UUID adId) throws NotFoundException;

    AdSeekerResponse createAd(AdSeekerRequest dto) throws ValidationException, NotFoundException;

    AdSeekerResponse updateAd(UUID adId, AdSeekerRequest dto) throws NotFoundException, AccessDeniedException, ValidationException;

    void deleteAd(UUID adId) throws NotFoundException, AccessDeniedException;
    List<AdSeekerResponse> findByFilter(AdSeekerFilter filter);

}
