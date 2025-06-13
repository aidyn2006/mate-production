package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import org.example.mateproduction.dto.request.AdImageRequest;
import org.example.mateproduction.dto.response.AdImageResponse;
import org.example.mateproduction.entity.Ad;
import org.example.mateproduction.entity.AdImage;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.AdImageRepository;
import org.example.mateproduction.repository.AdRepository;
import org.example.mateproduction.service.AdImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Deprecated
public class AdImageServiceImpl implements AdImageService {
    private final AdImageRepository adImageRepository;
    private final AdRepository adRepository;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public AdImageServiceImpl(AdImageRepository adImageRepository, AdRepository adRepository, CloudinaryService cloudinaryService) {
        this.adImageRepository = adImageRepository;
        this.adRepository = adRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public AdImageResponse createAdImage(AdImageRequest request) throws NotFoundException {
        Ad ad = adRepository.findById(request.getAdId())
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        String imageUrl = cloudinaryService.upload(request.getPhoto());

        AdImage adImage = new AdImage();
        adImage.setUrl(imageUrl);
        adImage.setAd(ad);
        adImage = adImageRepository.save(adImage);

        return mapToResponse(adImage);
    }

    public AdImageResponse getAdImageById(UUID id) throws NotFoundException {
        AdImage adImage = adImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ad image not found"));

        return mapToResponse(adImage);
    }

    @Transactional
    public AdImageResponse updateAdImage(UUID id, AdImageRequest request) throws NotFoundException {
        AdImage adImage = adImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ad image not found"));

        Ad ad = adRepository.findById(request.getAdId())
                .orElseThrow(() -> new NotFoundException("Ad not found"));

        String imageUrl = cloudinaryService.upload(request.getPhoto());

        adImage.setUrl(imageUrl);
        adImage.setAd(ad);

        adImage = adImageRepository.save(adImage);
        return mapToResponse(adImage);
    }

    @Transactional
    public void deleteAdImage(UUID id) throws NotFoundException {
        AdImage adImage = adImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ad image not found"));

        adImageRepository.delete(adImage);
    }

    public List<AdImageResponse> getAllAdImages() {
        return adImageRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AdImageResponse mapToResponse(AdImage adImage) {
        return AdImageResponse.builder()
                .id(adImage.getId())
                .url(adImage.getUrl())
                .adId(adImage.getAd().getId())
                .build();
    }
}
