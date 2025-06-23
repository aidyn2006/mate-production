package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.AdSeekerRequest;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.AdminReasonResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.example.mateproduction.util.Status;

import java.nio.file.AccessDeniedException;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface AdminSeekerAdService {

    /**
     * Получить все анкеты, находящиеся на модерации
     */
    Page<AdSeekerResponse> getAllModerateAds(int page, int size);

    /**
     * Подтвердить анкету
     */
    void approveAd(UUID adId);

    /**
     * Отклонить анкету с причиной
     */
    AdminReasonResponse rejectAd(UUID adId, String reason);

    /**
     * Принудительное редактирование анкеты
     */
    AdSeekerResponse updateAd(UUID adId, AdSeekerRequest dto) throws NotFoundException, AccessDeniedException, ValidationException;

    /**
     * Смена статуса анкеты (ACTIVE, REJECTED, DELETED и т.д.)
     */
    void changeAdStatus(UUID adId, Status newStatus, String reason);
}
