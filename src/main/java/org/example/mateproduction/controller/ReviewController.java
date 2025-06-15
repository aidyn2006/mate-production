package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.ReviewRequest;
import org.example.mateproduction.dto.response.ReviewResponse;
import org.example.mateproduction.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewRequest request) {
        ReviewResponse review = reviewService.createReview(request);
        return ResponseEntity.ok(review);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews(
            @RequestParam(required = false) UUID reviewerId,
            @RequestParam(required = false) UUID adId
    ) {
        List<ReviewResponse> reviews = reviewService.getAllReview(reviewerId, adId);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{reviewerId}/{adId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable UUID reviewerId,
            @PathVariable UUID adId,
            @RequestBody ReviewRequest request
    ) {
        ReviewResponse updated = reviewService.updateReview(reviewerId, adId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{reviewId}/{reviewerId}/{adId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID reviewId,
            @PathVariable UUID reviewerId,
            @PathVariable UUID adId
    ) {
        reviewService.deleteReview(reviewId, reviewerId, adId);
        return ResponseEntity.noContent().build();
    }
}
