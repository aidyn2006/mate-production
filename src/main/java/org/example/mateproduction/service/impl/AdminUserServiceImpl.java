package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.UserRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.dto.response.AdSeekerResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.AdSeeker;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.AdHouseRepository;
import org.example.mateproduction.repository.AdSeekerRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.AdminUserService;
import org.example.mateproduction.util.Status;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final AdHouseRepository adHouseRepository;
    private final AdSeekerRepository adSeekerRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<UserResponse> getAllUsers(boolean includeDeleted) {
        List<User> users = includeDeleted
                ? userRepository.findAll()
                : userRepository.findAll().stream()
                .filter(user -> !Boolean.TRUE.equals(user.getIsDeleted()))
                .toList();

        return users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(UUID userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(UUID userId, UserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            user.setAvatarUrl(cloudinaryService.upload(request.getAvatar()));
        }

        userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    public void deleteUserSoft(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void deleteUserHard(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public List<AdHouseResponse> getUserHouseAds(UUID userId) {
        List<AdHouse> ads = adHouseRepository.findAllByUserId(userId);
        return ads.stream()
                .filter(ad -> ad.getStatus() != Status.DELETED)
                .map(this::mapAdHouseToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdSeekerResponse> getUserSeekerAds(UUID userId) {
        List<AdSeeker> ads = adSeekerRepository.findAllByUserId(userId);
        return ads.stream()
                .filter(ad -> ad.getStatus() != Status.DELETED)
                .map(this::mapAdSeekerToResponse)
                .collect(Collectors.toList());
    }

    // ------------------ Мапперы ----------------------

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

    private AdHouseResponse mapAdHouseToResponse(AdHouse ad) {
        return AdHouseResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .price(ad.getPrice())
                .address(ad.getAddress())
                .city(ad.getCity())
                .user(mapToResponse(ad.getUser()))
                .type(ad.getType())
                .mainImageUrl(ad.getMainImageUrl())
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
                .moderationComment(ad.getModerationComment())
                .build();
    }

    private AdSeekerResponse mapAdSeekerToResponse(AdSeeker ad) {
        return AdSeekerResponse.builder()
                .id(ad.getId())
                .age(ad.getAge())
                .gender(ad.getGender())
                .user(mapToResponse(ad.getUser()))
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
                .build();
    }
}
