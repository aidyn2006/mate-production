package org.example.mateproduction.helpers;

import org.example.mateproduction.dto.request.AdSeekerFilter;
import org.example.mateproduction.entity.AdSeeker;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.ArrayList;
import java.util.List;

public class AdSeekerSpecification {

    public static Specification<AdSeeker> build(AdSeekerFilter filter) {
        return (Root<AdSeeker> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getMinAge() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("age"), filter.getMinAge()));

            if (filter.getMaxAge() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("age"), filter.getMaxAge()));

            if (filter.getGender() != null)
                predicates.add(cb.equal(root.get("gender"), filter.getGender()));

            if (filter.getCity() != null)
                predicates.add(cb.equal(root.get("city"), filter.getCity()));

            if (filter.getDesiredLocation() != null)
                predicates.add(cb.like(
                        cb.lower(root.get("desiredLocation")),
                        "%" + filter.getDesiredLocation().toLowerCase() + "%"
                ));

            if (filter.getMaxBudget() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("maxBudget"), filter.getMaxBudget()));

            if (filter.getEarliestMoveInDate() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("moveInDate"), filter.getEarliestMoveInDate()));

            if (filter.getHasFurnishedPreference() != null)
                predicates.add(cb.equal(root.get("hasFurnishedPreference"), filter.getHasFurnishedPreference()));

            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));

            if (filter.getRoommatePreferences() != null && !filter.getRoommatePreferences().isEmpty()) {
                predicates.add(root.get("preferredRoommateGender").in(filter.getRoommatePreferences()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
