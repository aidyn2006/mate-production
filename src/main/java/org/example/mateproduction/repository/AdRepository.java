package org.example.mateproduction.repository;

import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.util.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdRepository extends JpaRepository<AdHouse, UUID> {

    Integer countByUserAndStatus(User user, Status status);
    Optional<AdHouse> findByIdAndStatus(UUID id, Status status);

    List<AdHouse> findAllByStatus(Status status);

    List<AdHouse> findAllByUserId(UUID userId);


}

