package org.example.mateproduction.service;

import org.example.mateproduction.dto.response.ChartDataResponse;
import org.example.mateproduction.dto.response.DashboardStatsResponse;

public interface AdminDashboardService {
    DashboardStatsResponse getDashboardStats();
    ChartDataResponse getUserRegistrationsChart(String period);
    ChartDataResponse getListingCreationsChart(String period);
}