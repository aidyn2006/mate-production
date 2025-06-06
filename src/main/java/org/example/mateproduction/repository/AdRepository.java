package org.example.mateproduction.repository;

import org.example.mateproduction.entity.Ad;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.util.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdRepository extends JpaRepository<Ad, UUID> {

    Integer countByUserAndStatus(User user, Status status);
}

