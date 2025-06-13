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
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(
            @RequestParam(required = false) UUID reviewerId,
            @RequestParam(required = false) UUID adId
    ) {
        List<ReviewResponse> responses = reviewService.getAllReview(reviewerId, adId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping
    public ResponseEntity<ReviewResponse> updateReview(
            @RequestParam UUID reviewerId,
            @RequestParam UUID adId,
            @RequestBody ReviewRequest request
    ) {
        ReviewResponse response = reviewService.updateReview(reviewerId, adId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteReview(
            @RequestParam UUID reviewId,
            @RequestParam UUID reviewerId,
            @RequestParam UUID adId
    ) {
        reviewService.deleteReview(reviewId, reviewerId, adId);
        return ResponseEntity.noContent().build();
    }
}
