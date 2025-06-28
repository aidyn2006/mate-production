package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.response.ChartDataResponse;
import org.example.mateproduction.dto.response.DashboardStatsResponse;
import org.example.mateproduction.repository.AdHouseRepository;
import org.example.mateproduction.repository.AdSeekerRepository;
import org.example.mateproduction.repository.ReportRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.AdminDashboardService;
import org.example.mateproduction.util.ReportStatus;
import org.example.mateproduction.util.Status;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final AdHouseRepository adHouseRepository;
    private final AdSeekerRepository adSeekerRepository;
    private final ReportRepository reportRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        // Assuming your User entity has `createdAt` as a `Date` or `Timestamp`
        long newUsersToday = userRepository.countByCreatedAtAfter(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        long totalActiveListings = adHouseRepository.countByStatus(Status.ACTIVE) + adSeekerRepository.countByStatus(Status.ACTIVE);
        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING);

        return DashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .newUsersToday(newUsersToday)
                .totalActiveListings(totalActiveListings)
                .pendingReports(pendingReports)
                .build();
    }

    @Override
    public ChartDataResponse getUserRegistrationsChart(String period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = "monthly".equalsIgnoreCase(period) ? endDate.minusDays(30) : endDate.minusDays(7);

        Map<LocalDate, Long> data = userRepository.findUserRegistrationCounts(
                Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
        ).stream().collect(Collectors.toMap(
                // --- FIX APPLIED HERE ---
                tuple -> ((java.sql.Date) tuple[0]).toLocalDate(),
                tuple -> (Long) tuple[1]
        ));

        return buildChartData(startDate, endDate, data);
    }

    @Override
    public ChartDataResponse getListingCreationsChart(String period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = "monthly".equalsIgnoreCase(period) ? endDate.minusDays(30) : endDate.minusDays(7);
        Date startDateSql = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateSql = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());


        Map<LocalDate, Long> houseData = adHouseRepository.findListingCreationCounts(startDateSql, endDateSql)
                .stream().collect(Collectors.toMap(
                        // --- FIX APPLIED HERE ---
                        tuple -> ((java.sql.Date) tuple[0]).toLocalDate(),
                        tuple -> (Long) tuple[1]
                ));

        Map<LocalDate, Long> seekerData = adSeekerRepository.findListingCreationCounts(startDateSql, endDateSql)
                .stream().collect(Collectors.toMap(
                        // --- FIX APPLIED HERE ---
                        tuple -> ((java.sql.Date) tuple[0]).toLocalDate(),
                        tuple -> (Long) tuple[1]
                ));

        Map<LocalDate, Long> combinedData = new LinkedHashMap<>();
        houseData.forEach((key, value) -> combinedData.merge(key, value, Long::sum));
        seekerData.forEach((key, value) -> combinedData.merge(key, value, Long::sum));

        return buildChartData(startDate, endDate, combinedData);
    }

    private ChartDataResponse buildChartData(LocalDate startDate, LocalDate endDate, Map<LocalDate, Long> data) {
        Map<String, Long> completeData = new LinkedHashMap<>();
        // Use  ChronoUnit for a more robust way to calculate days
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));

        IntStream.range(0, (int) daysBetween)
                .mapToObj(startDate::plusDays)
                .forEach(date -> completeData.put(date.toString(), data.getOrDefault(date, 0L)));

        return new ChartDataResponse(
                new ArrayList<>(completeData.keySet()),
                new ArrayList<>(completeData.values())
        );
    }
}