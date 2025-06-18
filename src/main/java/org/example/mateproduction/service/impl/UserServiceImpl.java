package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.ChangePasswordRequest;
import org.example.mateproduction.dto.request.UserRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.AdSeeker;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.PasswordsNotMatchException;
import org.example.mateproduction.repository.AdHouseRepository;
import org.example.mateproduction.repository.AdSeekerRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final AdHouseRepository adRepository;
    private final AdSeekerRepository adSeekerRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    @Override
    public void deleteHardById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public void deleteById(UUID userId) {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsDeleted(true);
        userRepository.save(user);
    }

    public List<AdHouseResponse> getAllAdHouses() {
        UUID currentUserId = getCurrentUser().getId();
        Optional<User> userOpt = userRepository.findById(currentUserId);

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("У вас нету активных объявлений");
        }

        List<AdHouse> adHouses = adRepository.findAllByUserId(currentUserId);

        return adHouses.stream()
                .map(ad -> AdHouseResponse.builder()
                        .id(ad.getId())
                        .title(ad.getTitle())
                        .description(ad.getDescription())
                        .price(ad.getPrice())
                        .address(ad.getAddress())
                        .city(ad.getCity())
                        .user(UserResponse.builder()
                                .id(ad.getUser().getId())
                                .name(ad.getUser().getName())
                                .surname(ad.getUser().getSurname())
                                .username(ad.getUser().getUsername())
                                .email(ad.getUser().getEmail())
                                .phone(ad.getUser().getPhone())
                                .avatarUrl(ad.getUser().getAvatarUrl())
                                .role(ad.getUser().getRole())
                                .isVerified(ad.getUser().getIsVerified())
                                .build())
                        .type(ad.getType())
                        .status(ad.getStatus())
                        .images(ad.getImages())
                        .numberOfRooms(ad.getNumberOfRooms())
                        .area(ad.getArea())
                        .floor(ad.getFloor())
                        .furnished(ad.getFurnished())
                        .contactPhoneNumber(ad.getContactPhoneNumber())
                        .views(ad.getViews())
                        .createdAt(ad.getCreatedAt())
                        .updatedAt(ad.getUpdatedAt())
                        .build())
                .toList();
    }

    public List<AdSeekerResponse> getAllAdSeekers() {
        UUID currentUserId = getCurrentUser().getId();
        Optional<User> userOpt = userRepository.findById(currentUserId);

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("У вас нету активных объявлений");
        }

        List<AdSeeker> adSeekers = adSeekerRepository.findAllByUserId(currentUserId);

        return adSeekers.stream()
                .map(ad -> AdSeekerResponse.builder()
                        .id(ad.getId())
                        .age(ad.getAge())
                        .gender(ad.getGender())
                        .user(UserResponse.builder()
                                .id(ad.getUser().getId())
                                .name(ad.getUser().getName())
                                .surname(ad.getUser().getSurname())
                                .username(ad.getUser().getUsername())
                                .email(ad.getUser().getEmail())
                                .phone(ad.getUser().getPhone())
                                .avatarUrl(ad.getUser().getAvatarUrl())
                                .role(ad.getUser().getRole())
                                .isVerified(ad.getUser().getIsVerified())
                                .build())
                        .seekerDescription(ad.getSeekerDescription())
                        .city(ad.getCity())
                        .desiredLocation(ad.getDesiredLocation())
                        .maxBudget(ad.getMaxBudget())
                        .moveInDate(ad.getMoveInDate())
                        .hasFurnishedPreference(ad.getHasFurnishedPreference())
                        .roommatePreferences(ad.getRoommatePreferences())
                        .preferredRoommateGender(ad.getPreferredRoommateGender())
                        .status(ad.getStatus())
                        .views(ad.getViews())
                        .contactPhoneNumber(ad.getContactPhoneNumber())
                        .createdAt(ad.getCreatedAt())
                        .updatedAt(ad.getUpdatedAt())
                        .build())
                .toList();
    }


    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return mapToResponse(user);
    }

    public UserResponse updateUser(UUID userId, UserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setAvatarUrl(request.getAvatar()!=null ? cloudinaryService.upload(request.getAvatar()) : null);
        userRepository.save(user);

        return mapToResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) throws NotFoundException{
        // 1. Get the current user's email from the security context
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new NotFoundException("Current user not found in the database."));

        // 2. Check if the old password is correct
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new PasswordsNotMatchException("Incorrect old password.");
        }

        // 3. Check if the new password and confirmation match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordsNotMatchException("New passwords do not match.");
        }

        // 4. Encode and set the new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isVerified(user.getIsVerified())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }




}

