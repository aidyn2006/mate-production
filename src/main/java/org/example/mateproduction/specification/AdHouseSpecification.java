package org.example.mateproduction.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.mateproduction.dto.request.AdHouseFilter;
import org.example.mateproduction.entity.AdHouse;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AdHouseSpecification {

    public static Specification<AdHouse> findByCriteria(AdHouseFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // --- Text Search ---
            // If searchQuery is present, search in 'title' and 'description'
            if (StringUtils.hasText(filter.getSearchQuery())) {
                String likePattern = "%" + filter.getSearchQuery().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern)
                ));
            }

            // --- Price Range ---
            if (filter.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            // --- Number of Rooms ---
            if (filter.getMinRooms() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("numberOfRooms"), filter.getMinRooms()));
            }
            if (filter.getMaxRooms() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("numberOfRooms"), filter.getMaxRooms()));
            }

            // --- Area Range ---
            if (filter.getMinArea() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("area"), filter.getMinArea()));
            }
            if (filter.getMaxArea() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("area"), filter.getMaxArea()));
            }

            // --- Enum/Boolean Filters ---
            if (filter.getCity() != null) {
                predicates.add(criteriaBuilder.equal(root.get("city"), filter.getCity()));
            }
            if (filter.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), filter.getType()));
            }
            if (filter.getFurnished() != null) {
                predicates.add(criteriaBuilder.equal(root.get("furnished"), filter.getFurnished()));
            }

            // Always filter by a specific status, e.g., ACTIVE ads.
            // This is safer than allowing clients to request any status.
            predicates.add(criteriaBuilder.equal(root.get("status"), org.example.mateproduction.util.Status.ACTIVE));


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}