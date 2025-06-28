package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.response.ChartDataResponse;
import org.example.mateproduction.dto.response.DashboardStatsResponse;
import org.example.mateproduction.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(adminDashboardService.getDashboardStats());
    }

    @GetMapping("/charts/user-registrations")
    public ResponseEntity<ChartDataResponse> getUserRegistrationsChart(
            @RequestParam(defaultValue = "weekly") String period) {
        return ResponseEntity.ok(adminDashboardService.getUserRegistrationsChart(period));
    }

    @GetMapping("/charts/listing-creations")
    public ResponseEntity<ChartDataResponse> getListingCreationsChart(
            @RequestParam(defaultValue = "weekly") String period) {
        return ResponseEntity.ok(adminDashboardService.getListingCreationsChart(period));
    }
}