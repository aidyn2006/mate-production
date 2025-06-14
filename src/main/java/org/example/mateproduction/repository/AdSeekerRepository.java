package org.example.mateproduction.repository;

import org.example.mateproduction.entity.AdSeeker;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.util.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdSeekerRepository extends JpaRepository<AdSeeker, UUID> {

    List<AdSeeker> findAllByStatus(Status status);

    Optional<AdSeeker> findByIdAndStatus(UUID id, Status status);

    int countByUserAndStatus(User user, Status status);
}
