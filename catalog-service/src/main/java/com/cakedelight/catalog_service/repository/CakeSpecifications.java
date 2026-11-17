package com.cakedelight.catalog_service.repository;

import com.cakedelight.catalog_service.dto.CakeFilter;
import com.cakedelight.catalog_service.entity.Cake;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class CakeSpecifications {

    private CakeSpecifications() {
    }

    public static Specification<Cake> withFilter(CakeFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.name() != null && !filter.name().isBlank()) {
                String pattern = "%" + filter.name().trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.<String>get("name")), pattern));
            }

            if (filter.category() != null) {
                predicates.add(cb.equal(root.get("category"), filter.category()));
            }

            if (filter.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("price"), filter.minPrice()));
            }

            if (filter.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("price"), filter.maxPrice()));
            }

            if (filter.available() != null) {
                predicates.add(cb.equal(root.get("available"), filter.available()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}