package org.example.mateproduction.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryResponse {
    private long activeHouseAds;
    private long pendingHouseAds;
    private long rejectedHouseAds;
    private long totalHouseAdViews;

    private long activeSeekerAds;
    private long pendingSeekerAds;
    private long rejectedSeekerAds;
    private long totalSeekerAdViews;
}