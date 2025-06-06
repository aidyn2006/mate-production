package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.AdImageRequest;
import org.example.mateproduction.dto.response.AdImageResponse;
import org.example.mateproduction.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface AdImageService {
    AdImageResponse createAdImage(AdImageRequest request) throws NotFoundException;
    AdImageResponse getAdImageById(UUID id) throws NotFoundException;
    AdImageResponse updateAdImage(UUID id, AdImageRequest request) throws NotFoundException;
    void deleteAdImage(UUID id) throws NotFoundException;
    List<AdImageResponse> getAllAdImages();
}
