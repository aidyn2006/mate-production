package org.example.mateproduction.repository;

import org.example.mateproduction.entity.FavoriteSeeker;
import org.example.mateproduction.entity.contract.FavoriteSeekerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoriteSeekerRepository extends JpaRepository<FavoriteSeeker, FavoriteSeekerId> {
    List<FavoriteSeeker> findAllByUserId(UUID userId);
}