package org.example.mateproduction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface StreetRepository extends JpaRepository<Street, UUID> {
}
