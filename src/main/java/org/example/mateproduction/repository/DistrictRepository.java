package org.example.mateproduction.repository;

import org.example.mateproduction.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DistrictRepository extends JpaRepository<District, UUID> {
}
