package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdminReasonResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.util.Status;
import org.springframework.data.domain.Page;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface AdminHouseAdService {
    Page<AdHouseResponse> getAllModerateAds(int page, int size);
    void approveAd(UUID adId);
    AdminReasonResponse rejectAd(UUID adId, String reason);
    AdHouseResponse updateAd(UUID adId, AdHouseRequest dto) throws NotFoundException, AccessDeniedException, ValidationException;
    void changeAdStatus(UUID adId, Status newStatus, String reason);
}
