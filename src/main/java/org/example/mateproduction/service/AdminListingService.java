package org.example.mateproduction.service;

import org.example.mateproduction.dto.response.AdminListingDetailResponse;
import org.example.mateproduction.dto.response.AdminReasonResponse;
import org.example.mateproduction.entity.base.Ad;
import org.example.mateproduction.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminListingService<T extends Ad & Moderatable> {

    JpaRepository<T, UUID> getRepository();

    Page<?> findAll(UUID userId, Status status, Pageable pageable);

    AdminListingDetailResponse findById(UUID adId); // <-- ADD THIS

    void approveAd(UUID adId);

    AdminReasonResponse rejectAd(UUID adId, String reason);
    
    void deleteAd(UUID adId);

    void featureAd(UUID adId);

    void unfeatureAd(UUID adId);

    default void moderate(UUID adId, Status newStatus, String reason) {
        T ad = getRepository().findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad id: " + adId));

        ad.setStatus(newStatus);
        ad.setModerationComment(reason);
        getRepository().save(ad);
    }
}