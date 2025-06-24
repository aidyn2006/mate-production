package org.example.mateproduction.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.mateproduction.dto.request.AdSeekerFilter;
import org.example.mateproduction.entity.AdSeeker;
import org.example.mateproduction.util.Status;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AdSeekerSpecification {

    public static Specification<AdSeeker> findByCriteria(AdSeekerFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // --- Text Search in seekerDescription and desiredLocation ---
            if (StringUtils.hasText(filter.getSearchQuery())) {
                String likePattern = "%" + filter.getSearchQuery().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("seekerDescription")), likePattern),
                        cb.like(cb.lower(root.get("desiredLocation")), likePattern)
                ));
            }

            // --- Age Range ---
            if (filter.getMinAge() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("age"), filter.getMinAge()));
            }
            if (filter.getMaxAge() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("age"), filter.getMaxAge()));
            }

            // --- Gender ---
            if (filter.getGender() != null) {
                predicates.add(cb.equal(root.get("gender"), filter.getGender()));
            }

            // --- City ---
            if (filter.getCity() != null) {
                predicates.add(cb.equal(root.get("city"), filter.getCity()));
            }

            // --- Desired Location (exact match for more precision) ---
            if (StringUtils.hasText(filter.getDesiredLocation())) {
                String likePattern = "%" + filter.getDesiredLocation().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("desiredLocation")), likePattern));
            }

            // --- Max Budget ---
            if (filter.getMaxBudget() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("maxBudget"), filter.getMaxBudget()));
            }

            // --- Move-in Date (must be on or after this date) ---
            if (filter.getEarliestMoveInDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("moveInDate"), filter.getEarliestMoveInDate()));
            }

            // --- Furnished Preference ---
            if (filter.getHasFurnishedPreference() != null) {
                predicates.add(cb.equal(root.get("hasFurnishedPreference"), filter.getHasFurnishedPreference()));
            }

            // --- Preferred Roommate Preferences (check overlap) ---
            if (filter.getRoommatePreferences() != null && !filter.getRoommatePreferences().isEmpty()) {
                predicates.add(root.join("roommatePreferences").in(filter.getRoommatePreferences()));
            }

            // --- Status: Always filter by ACTIVE unless explicitly overridden ---
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            } else {
                predicates.add(cb.equal(root.get("status"), Status.ACTIVE));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
