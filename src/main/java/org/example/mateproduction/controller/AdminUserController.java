package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.UserRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // Получить всех пользователей (включая удалённых или нет)
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(defaultValue = "false") boolean includeDeleted) {
        return ResponseEntity.ok(adminUserService.getAllUsers(includeDeleted));
    }

    // Получить одного пользователя по ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) throws NotFoundException {
        return ResponseEntity.ok(adminUserService.getUserById(userId));
    }

    // Обновить пользователя
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @ModelAttribute UserRequest request
    ) {
        return ResponseEntity.ok(adminUserService.updateUser(userId, request));
    }

    // Мягкое удаление пользователя
    @DeleteMapping("/{userId}/soft")
    public ResponseEntity<Void> softDeleteUser(@PathVariable UUID userId) {
        adminUserService.deleteUserSoft(userId);
        return ResponseEntity.noContent().build();
    }

    // Жёсткое удаление пользователя
    @DeleteMapping("/{userId}/hard")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable UUID userId) {
        adminUserService.deleteUserHard(userId);
        return ResponseEntity.noContent().build();
    }

    // Получить все объявления о сдаче дома пользователя
    @GetMapping("/{userId}/house-ads")
    public ResponseEntity<List<AdHouseResponse>> getUserHouseAds(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminUserService.getUserHouseAds(userId));
    }

    // Получить все объявления-соискатели пользователя
    @GetMapping("/{userId}/seeker-ads")
    public ResponseEntity<List<AdSeekerResponse>> getUserSeekerAds(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminUserService.getUserSeekerAds(userId));
    }
}
