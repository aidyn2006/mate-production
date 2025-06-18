package org.example.mateproduction.controller;

public class AdminUserController {
    /*
    Base Path: /api/v1/admin/users

Purpose: The central hub for managing every user on the platform.

Methods:

GET /
    Action: Gets a paginated and searchable list of all users.
    Request Params: ?page=...&size=...&email=...&username=...&status=... (BANNED, VERIFIED, etc.)
    Response Model: Page<AdminUserSummaryResponse>
GET /{userId}
    Action: Fetches the complete, unfiltered details of a single user, including their listings, reports they've made, and reports against them.
    Response Model: AdminUserDetailResponse
POST /{userId}/ban
    Action: Bans a user, making them unable to log in or use the platform.
    Request Model: BanRequest
    Response Model: SuccessMessageResponse
POST /{userId}/unban
    Action: Lifts a ban on a user.
    Response Model: SuccessMessageResponse
POST /{userId}/verify
    Action: Manually marks a user's account as verified.
    Response Model: SuccessMessageResponse
PUT /{userId}/role
    Action: Changes a user's role (e.g., promote to ADMIN). Use with extreme caution.
    Request Model: UpdateUserRoleRequest
    Response Model: AdminUserDetailResponse
DELETE /{userId}
    Action: Performs a hard delete of a user and all their associated content (for GDPR compliance or severe violations).
    Response Model: ResponseEntity<Void>
     */
}
