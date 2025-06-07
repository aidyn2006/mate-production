package org.example.mateproduction.repository;

import org.example.mateproduction.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByReviewerIdAndAdvertisementId(UUID reviewerId, UUID adId);

    List<Review> findByReviewerId(UUID reviewerId);

    List<Review> findByAdvertisementId(UUID adId);
}
