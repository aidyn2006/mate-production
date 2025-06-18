package org.example.mateproduction.repository;

import org.example.mateproduction.entity.AdSeeker;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.util.CityNames;
import org.example.mateproduction.util.Gender;
import org.example.mateproduction.util.RoommatePreference;
import org.example.mateproduction.util.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdSeekerRepository extends JpaRepository<AdSeeker, UUID> {

    List<AdSeeker> findAllByStatus(Status status);

    Optional<AdSeeker> findByIdAndStatus(UUID id, Status status);

    int countByUserAndStatus(User user, Status status);

    List<AdSeeker> findAllByUserId(UUID userId);

    @Query("""
    SELECT s FROM AdSeeker s
    WHERE (:minAge IS NULL OR s.age >= :minAge)
      AND (:maxAge IS NULL OR s.age <= :maxAge)
      AND (:gender IS NULL OR s.gender = :gender)
      AND (:city IS NULL OR s.city = :city)
      AND (:desiredLocation IS NULL OR LOWER(s.desiredLocation) LIKE LOWER(CONCAT('%', :desiredLocation, '%')))
      AND (:maxBudget IS NULL OR s.maxBudget <= :maxBudget)
      AND (:moveInDate IS NULL OR s.moveInDate >= :moveInDate)
      AND (:hasFurnishedPreference IS NULL OR s.hasFurnishedPreference = :hasFurnishedPreference)
      AND (:preferredRoommateGender IS NULL OR s.preferredRoommateGender = :preferredRoommateGender)
      AND (:status IS NULL OR s.status = :status)
""")
    List<AdSeeker> findByFilter(
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("gender") Gender gender,
            @Param("city") CityNames city,
            @Param("desiredLocation") String desiredLocation,
            @Param("maxBudget") BigDecimal maxBudget,
            @Param("moveInDate") LocalDate moveInDate,
            @Param("hasFurnishedPreference") Boolean hasFurnishedPreference,
            @Param("preferredRoommateGender") List<RoommatePreference> preferredRoommateGender,
            @Param("status") Status status
    );

}
