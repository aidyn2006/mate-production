package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.ReviewRequest;
import org.example.mateproduction.dto.response.AdResponse;
import org.example.mateproduction.dto.response.ReviewResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.Ad;
import org.example.mateproduction.entity.Review;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.repository.AdRepository;
import org.example.mateproduction.repository.ReviewRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AdRepository adRepository;

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ad ad = adRepository.findById(request.getAdId())
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setUser(user);
        review.setAdvertisement(ad);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);

        return mapToResponse(saved);
    }

    @Override
    public List<ReviewResponse> getAllReview(UUID reviewerId, UUID adId) {
        List<Review> reviews;

        if (reviewerId != null && adId != null) {
            reviews = reviewRepository.findByReviewerIdAndAdvertisementId(reviewerId, adId);
        } else if (reviewerId != null) {
            reviews = reviewRepository.findByReviewerId(reviewerId);
        } else if (adId != null) {
            reviews = reviewRepository.findByAdvertisementId(adId);
        } else {
            reviews = reviewRepository.findAll();
        }

        return reviews.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse updateReview(UUID reviewerId, UUID adId, ReviewRequest request) {
        Review review = reviewRepository.findByReviewerIdAndAdvertisementId(reviewerId, adId)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updated = reviewRepository.save(review);

        return mapToResponse(updated);
    }

    @Override
    public void deleteReview(UUID reviewId, UUID reviewerId, UUID adId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Проверяем, что review принадлежит reviewerId и adId
        if (!review.getReviewer().getId().equals(reviewerId) || !review.getAdvertisement().getId().equals(adId)) {
            throw new RuntimeException("Review does not belong to given reviewer or ad");
        }

        reviewRepository.delete(review);
    }

    private ReviewResponse mapToResponse(Review review) {
        UserResponse reviewer = UserResponse.builder()
                .id(review.getReviewer().getId())
                .fullName(review.getReviewer().getFullName())
                .email(review.getReviewer().getEmail())
                .phone(review.getReviewer().getPhone())
                .role(review.getReviewer().getRole().name())
                .build();

        UserResponse user = UserResponse.builder()
                .id(review.getUser().getId())
                .fullName(review.getUser().getFullName())
                .email(review.getUser().getEmail())
                .phone(review.getUser().getPhone())
                .role(review.getUser().getRole().name())
                .build();

        AdResponse ad = AdResponse.builder()
                .id(review.getAdvertisement().getId())
                .title(review.getAdvertisement().getTitle())
                .description(review.getAdvertisement().getDescription())
                .build();

        return ReviewResponse.builder()
                .id(review.getId())
                .reviewer(reviewer)
                .user(user)
                .ad(ad)
                .rating(review.getRating())
                .comment(review.getComment())
                .build();
    }

}