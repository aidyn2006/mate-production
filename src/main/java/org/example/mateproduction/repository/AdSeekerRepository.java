package org.example.mateproduction.repository;

import org.example.mateproduction.entity.AdSeeker;
import org.example.mateproduction.entity.User;

import org.example.mateproduction.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdSeekerRepository extends JpaRepository<AdSeeker, UUID>, JpaSpecificationExecutor<AdSeeker> {

    Page<AdSeeker> findAllByStatus(Status status, Pageable pageable);

    Optional<AdSeeker> findByIdAndStatus(UUID id, Status status);

    int countByUserAndStatus(User user, Status status);

    List<AdSeeker> findAllByUserId(UUID userId);

    long countByStatus(Status status);

    @Query("SELECT FUNCTION('DATE', a.createdAt), COUNT(a) FROM AdSeeker a WHERE a.createdAt >= :startDate AND a.createdAt < :endDate GROUP BY FUNCTION('DATE', a.createdAt)")
    List<Object[]> findListingCreationCounts(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT COALESCE(SUM(a.views), 0) FROM AdSeeker a WHERE a.user.id = :userId")
    long sumViewsByUserId(@Param("userId") UUID userId);

    Integer countByUserIdAndStatus(UUID userId, Status status);
}
