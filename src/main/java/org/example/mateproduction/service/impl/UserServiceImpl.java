package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtService;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.dto.request.ChangePasswordRequest;
import org.example.mateproduction.dto.request.UserRequest;
import org.example.mateproduction.dto.response.*;
import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.AdSeeker;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.PasswordsNotMatchException;
import org.example.mateproduction.helpers.Auditable;
import org.example.mateproduction.repository.AdHouseRepository;
import org.example.mateproduction.repository.AdSeekerRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.UserService;
import org.example.mateproduction.util.Status;
import org.springframework.security.authentication.BadCredentialsException;
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

    private final AdSeekerRepository adSeekerRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AdHouseRepository adHouseRepository;

    @Override
    public UserResponse getById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    @Override
    public PublicUserResponse getPublicById(UUID userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId)); // Use NotFoundException
        return mapToPublicResponse(user);
    }

    @Override
    @Auditable(action = "HARD_DELETE_USER")
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

    @Auditable(action = "GET_ALL_HOUSE_BY")
    public List<AdHouseResponse> getAllAdHouses() {
        UUID currentUserId = getCurrentUserId();
        // DEPRECATED: чатгпт сказал что не нужно. Я не вижу причин в нём сомневаться
//                Optional<User> userOpt = userRepository.findById(currentUserId);
//
//        if (userOpt.isEmpty()) {
//            throw new UsernameNotFoundException("У вас нету активных объявлений");
//        }

        List<AdHouse> adHouses = adHouseRepository.findAllByUserId(currentUserId);


        return adHouses.stream()
                .filter(ad -> ad.getStatus() != Status.DELETED)
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
                .filter(ad -> ad.getStatus() != Status.DELETED)
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
        // This is fine, but can be slightly more direct
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return mapToResponse(user); // Important: mapToResponse should NOT include a token by default
    }

    public UUID getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new SecurityException("User is not authenticated");
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }

        throw new SecurityException("Invalid user principal");
    }

    public UserResponse updateUser(UUID userId, UserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // --- DEFINITIVE FIX: DEFENSIVE UPDATE LOGIC ---

        // Only update a field if a new, non-blank value was provided.
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        if (request.getSurname() != null && !request.getSurname().isBlank()) {
            user.setSurname(request.getSurname());
        }

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        // The phone field can be optional, so we allow setting it even if blank (to clear it).
        // If you want to prevent clearing, add `!request.getPhone().isBlank()`.
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        // VERY IMPORTANT: We are INTENTIONALLY NOT allowing email changes here.
        // Changing the primary identifier for login and JWT is complex and risky.

        // This check is crucial. It ensures we only try to upload a new avatar
        // and update the URL if a NEW file is actually included in the request.
        // This prevents the avatar from being deleted.
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            user.setAvatarUrl(cloudinaryService.upload(request.getAvatar()));
        }

        User savedUser = userRepository.save(user);

        // This part is correct: always generate a new token with the potentially updated details.
        String newToken = jwtService.generateToken(new JwtUserDetails(savedUser));
        UserResponse response = mapToResponse(savedUser);
        response.setToken(newToken);

        return response;
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 1. Verify the old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect old password");
        }

        // 2. Encode and set the new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public DashboardSummaryResponse getDashboardSummary() {
        UUID currentUserId = getCurrentUserId();

        // Get counts for AdHouse
        long activeHouseAds = adHouseRepository.countByUserIdAndStatus(currentUserId, Status.ACTIVE);
        long pendingHouseAds = adHouseRepository.countByUserIdAndStatus(currentUserId, Status.MODERATION);
        long rejectedHouseAds = adHouseRepository.countByUserIdAndStatus(currentUserId, Status.REJECTED);
        long totalHouseAdViews = adHouseRepository.sumViewsByUserId(currentUserId);

        // Get counts for AdSeeker
        long activeSeekerAds = adSeekerRepository.countByUserIdAndStatus(currentUserId, Status.ACTIVE);
        long pendingSeekerAds = adSeekerRepository.countByUserIdAndStatus(currentUserId, Status.MODERATION);
        long rejectedSeekerAds = adSeekerRepository.countByUserIdAndStatus(currentUserId, Status.REJECTED);
        long totalSeekerAdViews = adSeekerRepository.sumViewsByUserId(currentUserId);


        return DashboardSummaryResponse.builder()
                .activeHouseAds(activeHouseAds)
                .pendingHouseAds(pendingHouseAds)
                .rejectedHouseAds(rejectedHouseAds)
                .totalHouseAdViews(totalHouseAdViews)
                .activeSeekerAds(activeSeekerAds)
                .pendingSeekerAds(pendingSeekerAds)
                .rejectedSeekerAds(rejectedSeekerAds)
                .totalSeekerAdViews(totalSeekerAdViews)
                .build();
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
                .createdAt(user.getCreatedAt())
                .isDeleted(user.getIsDeleted())
                .build();
    }

    // Add a new mapping method
    private PublicUserResponse mapToPublicResponse(User user) {
        return PublicUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }




}

