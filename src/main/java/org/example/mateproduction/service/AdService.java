package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.AdRequest;
import org.example.mateproduction.dto.response.AdResponse;
import org.example.mateproduction.entity.Ad;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Service
public interface AdService {

    AdResponse createAd(AdRequest dto, UUID userId) throws ValidationException, NotFoundException;
    AdResponse getAdById(UUID adId) throws NotFoundException;
    AdResponse updateAd(UUID adId, AdRequest dto, UUID currentUserId) throws NotFoundException, AccessDeniedException, ValidationException;
    void deleteAd(UUID adId, UUID currentUserId) throws AccessDeniedException, NotFoundException;

    List<AdResponse> getAllAds();
}
