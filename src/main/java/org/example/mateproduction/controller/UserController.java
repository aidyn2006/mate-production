package org.example.mateproduction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.dto.request.ChangePasswordRequest;
import org.example.mateproduction.dto.request.UserRequest;
import org.example.mateproduction.dto.response.*;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) throws NotFoundException {
        // Step 1: Get the current authenticated user's details
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Step 2: Check if the principal is a valid, authenticated user
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails currentUser) {
            // Step 3: Compare the requested ID with the current user's ID
            if (currentUser.getId().equals(id)) {
                // If they match, issue a redirect to the more specific "/me" endpoint.
                return ResponseEntity
                        .status(HttpStatus.FOUND) // HTTP 302
                        .location(URI.create("/api/v1/users/me"))
                        .build();
            }
        }

        // Step 4: If it's not the current user or the user is not logged in,
        // proceed to fetch the public profile as normal.
        PublicUserResponse publicUser = userService.getPublicById(id);
        return ResponseEntity.ok(publicUser);
    }

    @DeleteMapping("/hard/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {
        userService.deleteHardById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateMyProfile(@ModelAttribute UserRequest request) {
        // A more direct way to get the current user's ID
        var principal = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID currentUserId = principal.getId();
        UserResponse updatedUser = userService.updateUser(currentUserId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        var principal = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID currentUserId = principal.getId();
        userService.changePassword(currentUserId, request);
        return ResponseEntity.ok().build();
    }


    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me/ads/houses")
    public ResponseEntity<List<AdHouseResponse>> getMyAdHouses() {
        return ResponseEntity.ok(userService.getAllAdHouses());
    }

    @GetMapping("/me/ads/seekers")
    public ResponseEntity<List<AdSeekerResponse>> getMyAdSeeker() {
        return ResponseEntity.ok(userService.getAllAdSeekers());
    }

    @GetMapping("/me/ads/summary")
    public ResponseEntity<DashboardSummaryResponse> getMyAdsSummary() {
        return ResponseEntity.ok(userService.getDashboardSummary());
    }
}
