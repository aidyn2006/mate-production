package org.example.mateproduction.controller;

public class AdminReportController {
    /*
    Base Path: /api/v1/admin/reports

Purpose: Provides the interface for the moderation queue where admins review user-submitted reports.

Methods:

GET /
    Action: Fetches a paginated list of reports, typically filtered for PENDING status.
    Request Params: ?page=...&status=PENDING
    Response Model: Page<ReportSummaryResponse>
GET /{reportId}
    Action: Gets the full details of a specific report, including the content being reported.
    Response Model: ReportDetailResponse
POST /{reportId}/resolve
    Action: Allows an admin to take action on a report (e.g., dismiss it, ban the user, delete the content). The service layer will contain the logic to execute the chosen action.
    Request Model: ResolveReportRequest
    Response Model: ReportDetailResponse
     */
}
