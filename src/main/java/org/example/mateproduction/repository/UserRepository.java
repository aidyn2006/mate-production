package org.example.mateproduction.repository;

import org.example.mateproduction.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    Page<User> findAll(Specification<User> spec, Pageable pageable);

    @Query("SELECT FUNCTION('DATE', u.createdAt), COUNT(u) FROM User u WHERE u.createdAt >= :startDate AND u.createdAt < :endDate GROUP BY FUNCTION('DATE', u.createdAt)")
    List<Object[]> findUserRegistrationCounts(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    long countByCreatedAtAfter(Date date);

}
