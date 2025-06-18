package org.example.mateproduction.controller;

public class AdminListingController {
    /*
    AdminListingController
Base Path: /api/v1/admin/listings

Purpose: To moderate, manage, and feature all AdHouse and AdSeeker listings.

Methods:

GET /
    Action: Gets a paginated list of all listings (both types).
    Request Params: ?page=...&type=...&userId=...&status=...&isFeatured=...
    Response Model: Page<AdminListingSummaryResponse>
PUT /{type}/{adId}
    Action: Allows an admin to forcefully edit any listing to remove inappropriate content.
    Path Vars: {type} is either houses or seekers.
    Request Model: AdHouseRequest or AdSeekerRequest
    Response Model: AdHouseDetailResponse or AdSeekerDetailResponse
DELETE /{type}/{adId}
    Action: Allows an admin to forcefully delete any listing.
    Path Vars: {type} is either houses or seekers.
    Response Model: ResponseEntity<Void>
POST /{type}/{adId}/feature
    Action: Marks a listing as "featured" to appear on the homepage.
    Response Model: SuccessMessageResponse
DELETE /{type}/{adId}/feature
    Action: Removes the "featured" status from a listing.
    Response Model: SuccessMessageResponse
     */
}
