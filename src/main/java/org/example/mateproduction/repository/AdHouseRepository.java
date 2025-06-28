package org.example.mateproduction.repository;

import org.example.mateproduction.entity.AdHouse;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.util.AdType;
import org.example.mateproduction.util.CityNames;
import org.example.mateproduction.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdHouseRepository extends JpaRepository<AdHouse, UUID>, JpaSpecificationExecutor<AdHouse> {

    Integer countByUserAndStatus(User user, Status status);
    Optional<AdHouse> findByIdAndStatus(UUID id, Status status);

    Page<AdHouse> findAllByStatus(Status status, Pageable pageable);

    List<AdHouse> findAllByUserId(UUID userId);

    @Query("""
    SELECT a FROM AdHouse a
    WHERE (:minPrice IS NULL OR a.price >= :minPrice)
      AND (:maxPrice IS NULL OR a.price <= :maxPrice)
      AND (:minRooms IS NULL OR a.numberOfRooms >= :minRooms)
      AND (:maxRooms IS NULL OR a.numberOfRooms <= :maxRooms)
      AND (:minArea IS NULL OR a.area >= :minArea)
      AND (:maxArea IS NULL OR a.area <= :maxArea)
      AND (:city IS NULL OR a.city = :city)
      AND (:type IS NULL OR a.type = :type)
      AND (:furnished IS NULL OR a.furnished = :furnished)
      AND (:status IS NULL OR a.status = :status)
""")
    Page<AdHouse> findByFilter(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRooms") Integer minRooms,
            @Param("maxRooms") Integer maxRooms,
            @Param("minArea") Double minArea,
            @Param("maxArea") Double maxArea,
            @Param("city") CityNames city,
            @Param("type") AdType type,
            @Param("furnished") Boolean furnished,
            @Param("status") Status status,
            Pageable pageable
    );

    long countByStatus(Status status);

    @Query("SELECT FUNCTION('DATE', a.createdAt), COUNT(a) FROM AdHouse a WHERE a.createdAt >= :startDate AND a.createdAt < :endDate GROUP BY FUNCTION('DATE', a.createdAt)")
    List<Object[]> findListingCreationCounts(@Param("startDate") Date startDate, @Param("endDate") Date endDate);


}

