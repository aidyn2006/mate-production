package org.example.mateproduction.repository;

import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.Report;
import org.example.mateproduction.util.ReportStatus;
import org.example.mateproduction.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.mateproduction.util.ReportableType;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    /**
     * Checks if a user has already reported a specific entity.
     * This prevents duplicate reports from the same user for the same item.
     *
     * @param reporterId The ID of the user making the report.
     * @param reportedEntityId The ID of the entity being reported.
     * @param reportedEntityType The type of the entity being reported.
     * @return An Optional containing the Report if it exists.
     */
    Optional<Report> findByReporterIdAndReportedEntityIdAndReportedEntityType(
            UUID reporterId, UUID reportedEntityId, ReportableType reportedEntityType);

    List<Report> findAllByReporterId(UUID reporterId);
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
    long countByStatus(ReportStatus status);
}