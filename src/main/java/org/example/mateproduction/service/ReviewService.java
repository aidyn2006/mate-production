package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.ReviewRequest;
import org.example.mateproduction.dto.response.ReviewResponse;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request);
    List<ReviewResponse> getAllReview(UUID reviewerId, UUID adId);

    ReviewResponse updateReview(UUID reviewerId, UUID adId, ReviewRequest request);

    void deleteReview(UUID reviewId, UUID reviewerId, UUID adID);
}
