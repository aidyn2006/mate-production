package org.example.mateproduction.repository;

import org.example.mateproduction.entity.FavoriteHouse;
import org.example.mateproduction.entity.contract.FavoriteHouseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoriteHouseRepository extends JpaRepository<FavoriteHouse, FavoriteHouseId> {
    List<FavoriteHouse> findAllByUserId(UUID userId);
}