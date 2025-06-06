package org.example.mateproduction.repository;

import org.example.mateproduction.entity.AdImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdImageRepository extends JpaRepository<AdImage, UUID> {
}
