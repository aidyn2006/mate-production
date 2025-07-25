package org.example.mateproduction.factory;

import org.example.mateproduction.service.AdminListingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AdminServiceFactory {

    private final AdminListingService<?> houseService;
    private final AdminListingService<?> seekerService;

    public AdminServiceFactory(
            @Qualifier("adminHouseService") AdminListingService<?> houseService,
            @Qualifier("adminSeekerService") AdminListingService<?> seekerService) {
        this.houseService = houseService;
        this.seekerService = seekerService;
    }

    public AdminListingService<?> getService(String type) {
        if ("house".equalsIgnoreCase(type)) {
            return houseService;
        } else if ("seeker".equalsIgnoreCase(type)) {
            return seekerService;
        }
        throw new IllegalArgumentException("Unknown listing type: " + type);
    }
}