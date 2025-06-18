package org.example.mateproduction.controller;

public class AdminDashboardController {
    /*
    Base Path: /api/v1/admin/dashboard

Purpose: Provides a high-level overview of the platform's health and activity through statistics and chart data.

Methods:

GET /stats
    Action: Fetches key metrics for the main admin dashboard display.
    Response Model: DashboardStatsResponse
GET /charts/user-registrations
    Action: Provides time-series data for user sign-ups.
    Request Params: ?period=weekly (or daily, monthly)
    Response Model: ChartDataResponse
GET /charts/listing-creations
    Action: Provides time-series data for new ad creations.
    Request Params: ?period=weekly
    Response Model: ChartDataResponse
     */
}
