package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @DeleteMapping("/hard/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {
        userService.deleteHardById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteByid(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
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
}
