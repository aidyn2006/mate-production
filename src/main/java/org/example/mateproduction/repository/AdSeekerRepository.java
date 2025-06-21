package org.example.mateproduction.repository;

import org.example.mateproduction.entity.AdSeeker;
import org.example.mateproduction.entity.User;

import org.example.mateproduction.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdSeekerRepository extends JpaRepository<AdSeeker, UUID>, JpaSpecificationExecutor<AdSeeker> {

    Page<AdSeeker> findAllByStatus(Status status, Pageable pageable);

    Optional<AdSeeker> findByIdAndStatus(UUID id, Status status);

    int countByUserAndStatus(User user, Status status);

    List<AdSeeker> findAllByUserId(UUID userId);
}
