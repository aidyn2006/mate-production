package org.example.mateproduction.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalUsers;
    private long newUsersToday;
    private long totalActiveListings;
    private long pendingReports;
}